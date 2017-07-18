package org.todss.algorithm.impl;

import org.todss.algorithm.Algorithm;
import org.todss.algorithm.AlgorithmContext;
import org.todss.algorithm.model.*;
import org.todss.algorithm.path.Path;
import org.todss.algorithm.path.PathUtilities;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static org.todss.algorithm.Constants.MAX_INTAKE_MOMENTS;

/**
 * A class representing our smart algorithm implementation.
 * @author Displee
 */
public class SmartAlgorithm implements Algorithm {

	@Override
	public String name() {
		return "Smart-algorithm";
	}

	/**
	 * Get the date range in which we calculate the new intake moments.
	 * @param travels A list of travels.
	 * @return An array with containing 2 values, the first index is the start date, the second one is the end date.
	 */
	private static ZonedDateTime[] getRange(List<Travel> travels, Frequency frequency) {
		final ZonedDateTime[] dates = new ZonedDateTime[2];
		ZonedDateTime start = null;
		ZonedDateTime end = null;
		for(Travel travel : travels) {
			if (start == null || travel.getDeparture().isBefore(start)) {
				start = travel.getDeparture();
			}
			if (end == null || travel.getArrival().isAfter(end)) {
				end = travel.getDeparture();
			}
		}
		if (start == null || end == null) {
			return null;
		}
		dates[0] = getNextIntakeDate(start, frequency, -MAX_INTAKE_MOMENTS).withHour(0).withMinute(0).withSecond(0).withNano(0);
		//end date is excluded so +1
		dates[1] = getNextIntakeDate(end, frequency, MAX_INTAKE_MOMENTS + 1).withHour(0).withMinute(0).withSecond(0).withNano(0);
		return dates;
	}

	@Override
	public List<Intake> run(AlgorithmContext context) {
		final Frequency frequency = context.getAlarm().getFrequency();
		final ZonedDateTime[] range = getRange(context.getTravels(), frequency);
		if (range == null) {
			return null;
		}
		final long daysLength = Duration.between(range[0], range[1]).toDays();
		final List<Intake> list = new ArrayList<>();
		final int length = (int) daysLength * (24 / frequency.getHours());
		for(int i = 0; i < length; i++) {
			list.add(null);
		}
		ZoneId currentZone = range[0].getZone();
		outer: for(int i = 0; i < length; i++) {
			ZonedDateTime current = getNextIntakeDate(range[0].withHour(context.getAlarm().getStart().getHour()), frequency, i).withZoneSameLocal(currentZone);
			for(Travel travel : context.getTravels()) {
				final ZonedDateTime departure = travel.getDeparture();
				final ZonedDateTime arrival = travel.getArrival();
				if (departure.isAfter(arrival)) {
					continue;
				}
				//TODO Side note: travel must be MAX_INTAKE_MOMENTS * 2 long.
				//So for example if we have an alarm with a daily frequency
				//the travel must be (MAX_INTAKE_MOMENTS * frequency.getHours()) * 2 hours long = 192 hours = 8 days
				if (current.getYear() == departure.getYear() && current.getDayOfYear() == departure.getDayOfYear()) {
					final int difference = travel.getDifference();
					final int margin = frequency.getMargin();
					if (difference < -margin || difference > margin) {
						//Time difference has exceeded the margin of the frequency, so demarcate or plan an extra intake moment
						final int maxHours = frequency.getMargin() * MAX_INTAKE_MOMENTS;
						if ((difference <= -maxHours || difference >= maxHours) || forceExtraIntake(context.getAlarm())) {
							//TODO Plan an extra intake moment. At this moment this is discouraged.
							//Because of the fact that a computer application tells you when to take an extra medicine.
							throw new UnsupportedOperationException("Maximum amount of intakes exceeded, extra intakes are not handled yet (difference=" + difference + ").");
						} else {
							//Demarcate before and after and see which one is the best.
							final boolean afterwards;
							DemarcateResult beforeResult = demarcate(current, travel, difference, frequency, i, false);
							DemarcateResult afterResult = demarcate(current, travel, -difference, frequency, i, true);
							DemarcateResult result;
							if (beforeResult == null) {
								result = afterResult;
								afterwards = true;
							} else if (afterResult == null) {
								result = beforeResult;
								afterwards = false;
							} else {
								boolean useAfter = beforeResult.compareInvalidIntakes(context.getAlarm().getStart().getHour(), afterResult);
								if (!useAfter || Math.abs(beforeResult.getPath().getCost()) < Math.abs(afterResult.getPath().getCost())) {
									result = beforeResult;
									afterwards = false;
								} else {
									result = afterResult;
									afterwards = true;
								}
							}
							if (result == null) {
								throw new RuntimeException("Impossible travel[difference=" + difference + ", travel=" + travel + "]");
							}
							result.populate(list);
							if (afterwards) {
								if (result.addCurrent()) {
									list.set(i, new Intake(current));
								}
								i += result.getPath().getSteps().length - 1;
							}
							currentZone = arrival.getZone();
							continue outer;
						}
					} else {
						//Time difference is within the margin, so we do nothing.
						currentZone = arrival.getZone();
					}
				} else if (current.getYear() == arrival.getYear() && current.getDayOfYear() == arrival.getDayOfYear()) {
					currentZone = arrival.getZone();
					current = current.withZoneSameLocal(currentZone);
				}
			}
			list.set(i, new Intake(current));
		}
		return list;
	}

	/**
	 * Get the next intake date.
	 * @param current The start date.
	 * @param frequency The frequency.
	 * @return The next intake date.
	 */
	public static ZonedDateTime getNextIntakeDate(ZonedDateTime current, Frequency frequency) {
		return getNextIntakeDate(current, frequency, 1);
	}

	/**
	 * Get the next default intake date, based on the frequency and the amount of intakes to pass or to go back.
	 * @param current The start date.
	 * @param frequency The frequency.
	 * @param amount The amount of intakes to pass or to go back (use negative values to go back).
	 * Example:
	 * <pre>
	 * {@code
	 * getNextIntakeDate(date, frequency, 1); --> the next intake date
	 * getNextIntakeDate(date, frequency, -1; --> the previous intake date
	 * getNextIntakeDate(date, frequency, 2); --> the second next intake date
	 * etc...
	 * }
	 * </pre>
	 * @return The intake date.
	 */
	public static ZonedDateTime getNextIntakeDate(ZonedDateTime current, Frequency frequency, int amount) {
		if (amount == 0) {
			return current.plusHours(0);//return a copy
		}
		final int hours = frequency.getHours() * amount;
		final ZonedDateTime date = current.plusHours(hours);
		final ZoneId zone = current.getZone();
		final boolean currentDST = zone.getRules().isDaylightSavings(Instant.from(current));
		final boolean dateDST = zone.getRules().isDaylightSavings(Instant.from(date));
		if (!currentDST && dateDST) {
			final int targetHour = (current.getHour() + hours) % 24;
			//If the hour of the previous day is two, and we go from winter to summer time, that means we can't do minus 1 hour
			//because the first day in summer time 02:00 doesn't exist, it go's to 03:00, so we don't minus 1 hour
			if (targetHour == 2) {
				return date;
			}
			return date.minusHours(1);
		} else if (currentDST && !dateDST) {
			return date.plusHours(1);
		}
		return date;
	}

	/**
	 * Check if we have to force an extra intake moment.
	 * @param alarm The alarm.
	 * @return If we have to force an extra intake moment.
	 */
	private boolean forceExtraIntake(Alarm alarm) {
		return false;
	}

	/**
	 * Find the available paths that can be taken.
	 * @param steps The minimum amount of steps.
	 * @param difference The time difference.
	 * @param start The start date.
	 * @param travel The travel.
	 * @param frequency The frequency.
	 * @param after If we demarcate after the travel.
	 * @return A list of paths.
	 */
	private List<Path> findAvailablePaths(int steps, int difference, ZonedDateTime start, Travel travel, Frequency frequency, boolean after) {
		if (steps > MAX_INTAKE_MOMENTS) {
			return null;
		}
		List<Path> availablePaths = null;
		while(steps <= MAX_INTAKE_MOMENTS && (availablePaths == null || availablePaths.size() == 0)) {
			//if no paths are found, keep trying until we found possible paths to take.
			availablePaths = PathUtilities.findPathsForTargetDate(steps++, difference, start, travel, frequency, after);
		}
		return (availablePaths == null || availablePaths.size() == 0) ? null : availablePaths;
	}

	/**
	 * Finally, start the demarcation, calculate new intakes and choose the best one.
	 * @param current The current date.
	 * @param travel The travel.
	 * @param difference The time difference.
	 * @param frequency The frequency.
	 * @param currentIndex The current index.
	 * @param after If we have to demarcate after the travel.
	 * @return The best demarcation result that can be used for a patient.
	 */
	private DemarcateResult demarcate(ZonedDateTime current, Travel travel, int difference, Frequency frequency, int currentIndex, boolean after) {
		//The minimum amount of steps we need to take.
		final int steps = (int) Math.ceil(difference / (double) (difference < 0 ? -frequency.getMargin() : frequency.getMargin()));
		//Our date object we will work with for the demarcation process.
		ZonedDateTime previous = getNextIntakeDate(current, frequency, after ? 0 : 1);
		//A copy of the previous variable.
		final ZonedDateTime backup = previous.minusHours(0);
		//Get all paths that are available to use.
		final List<Path> availablePaths = findAvailablePaths(steps, difference, previous, travel, frequency, after);
		if (availablePaths == null) {
			//No path could be found.
			return null;
		}
		final List<DemarcateResult> results = new ArrayList<>();
		DemarcateResult finalResult = null;
		//Demarcate all available paths and return the best one.
		while (true) {
			final Path path = PathUtilities.getShortestPath(availablePaths);
			DemarcateResult result = PathUtilities.process(path, travel, backup.plusHours(0), frequency, currentIndex, after);
			if (result.isValid()) {
				//if the result is valid
				finalResult = result;
				break;
			} else {
				results.add(result);
				availablePaths.remove(path);
				if (availablePaths.isEmpty()) {
					//if no paths are left, determine the best result that can be taken
					DemarcateResult bestResult = null;
					for (DemarcateResult r : results) {
						if (bestResult == null || (bestResult.compareInvalidIntakes(backup.getHour(), r) || Math.abs(r.getPath().getCost()) < Math.abs(bestResult.getPath().getCost()))) {
							bestResult = r;
						}
					}
					if (bestResult != null) {
						finalResult = bestResult;
					}
					break;
				}
			}
		}
		return finalResult;
	}

}
