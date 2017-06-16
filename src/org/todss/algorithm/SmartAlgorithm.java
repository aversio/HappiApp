package org.todss.algorithm;

import org.todss.algorithm.path.Path;
import org.todss.algorithm.path.PathUtilities;
import org.todss.model.Alarm;
import org.todss.model.Frequency;
import org.todss.model.IntakeMoment;
import org.todss.model.Travel;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static org.todss.Constants.MAX_INTAKE_MOMENTS;

public class SmartAlgorithm {

	public static ZonedDateTime[] getRange(List<Travel> travels) {
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
		dates[0] = start.minusDays(3).withMinute(0);
		dates[1] = end.plusDays(4).withMinute(0);
		return dates;
	}

	public static List<IntakeMoment> run(AlgorithmContext context) {
		final ZonedDateTime[] range = getRange(context.getTravels());
		if (range == null) {
			return null;
		}
		final long daysLength = Duration.between(range[0], range[1]).toDays();
		final Frequency frequency = context.getAlarm().getFrequency();
		final List<IntakeMoment> list = new ArrayList<>();
		final int length = (int) daysLength * (frequency.getHours() == 24 ? 1 : 2);
		System.out.println("Settings[freq=" + frequency.getHours() + ", intakes_length=" + length + ", period=" + daysLength + "]");
		for(int i = 0; i < length; i++) {
			list.add(null);
		}
		ZoneId currentZone = range[0].getZone();
		outer: for(int i = 0; i < length; i++) {
			ZonedDateTime current = getNextIntakeDate(range[0], frequency, i).withZoneSameLocal(currentZone).withHour(context.getAlarm().getStart().getHour());
			for(Travel travel : context.getTravels()) {
				final ZonedDateTime departure = travel.getDeparture();
				final ZonedDateTime arrival = travel.getArrival();
				if (current.getYear() == departure.getYear() && current.getDayOfYear() == departure.getDayOfYear()) {
					final int difference = calculateDifference(departure, arrival);
					final int margin = frequency.getMargin();
					if (difference < -margin || difference > margin) {
						//Time difference is too big, so demarcate or plan an extra intake moment
						final int maxHours = frequency.getHours() / 2;
						if ((difference <= -maxHours || difference >= maxHours) || forceExtraIntake(context.getAlarm())) {
							//TODO Extra intake moment.
						} else {
							//Afbakenen
							ZonedDateTime temp = current.minusHours(difference);
							//TODO Support voor vroege vogels
							final boolean afterwards = temp.getHour() > 22 || temp.getHour() < 8;
							if (afterwards) {
								list.set(i, new IntakeMoment(current));
							}
							final int overflow = demarcate(current, arrival, difference, frequency, i, list, afterwards);
							if (afterwards) {
								i += overflow;
								currentZone = arrival.getZone();//TODO Fix deze?
							}
							currentZone = arrival.getZone();//TODO Fix deze?
							continue outer;
						}
					} else {
						//Time difference is within the margin
						current = current.minusHours(difference);
					}
				} else if (current.getYear() == arrival.getYear() && current.getDayOfYear() == arrival.getDayOfYear()) {
					currentZone = arrival.getZone();
					current = current.withZoneSameLocal(currentZone);
				}
			}
			list.set(i, new IntakeMoment(current));
		}
		writeIntakes(list);
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
	 * @param amount The amount of intakes to pass or to go back.
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
			return current;
		}
		final int hours = frequency.getHours() * amount;
		final ZonedDateTime date = current.plusHours(hours);
		final ZoneId zone = current.getZone();
		final boolean currentDST = zone.getRules().isDaylightSavings(Instant.from(current));
		final boolean dateDST = zone.getRules().isDaylightSavings(Instant.from(date));
		if (!currentDST && dateDST) {
			final int targetHour = (current.getHour() + hours) % 24;
			//If the hour of the previous day is two, and we go to summer time, that means we can't do minus 1 hour
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
	 * Calculate the hour difference of two zoned date time objects.
	 * @param departure The departure date.
	 * @param arrival The arrival date.
	 * @return The difference in hours.
	 */
	private static int calculateDifference(ZonedDateTime departure, ZonedDateTime arrival) {
		final int arrivalOffset = departure.getZone().getRules().getOffset(arrival.toLocalDateTime()).getTotalSeconds() / 3600;
		final int departureOffset = arrival.getZone().getRules().getOffset(departure.toLocalDateTime()).getTotalSeconds() / 3600;
		return departureOffset - arrivalOffset;
	}

	/**
	 * Check if we have to force an extra intake moment.
	 * @param alarm The alarm.
	 * @return If we have to force an extra intake moment.
	 */
	private static boolean forceExtraIntake(Alarm alarm) {
		return false;
	}

	private static List<Path> findAvailablePaths(int min, int difference, ZonedDateTime start, ZonedDateTime arrival, Frequency frequency) {
		List<Path> availablePaths = PathUtilities.findPathsAfterArrival(min, difference, start, arrival, frequency);
		if (min != MAX_INTAKE_MOMENTS && availablePaths.size() == 0) {
			availablePaths = PathUtilities.findPathsAfterArrival(MAX_INTAKE_MOMENTS, difference, start, arrival, frequency);
		}
		return availablePaths.size() == 0 ? null : availablePaths;
	}

	private static int demarcate(ZonedDateTime current, ZonedDateTime arrival, int difference, Frequency frequency, int index, List<IntakeMoment> list, boolean after) {
		final int min = (int) Math.ceil(difference / (double) (difference < 0 ? -frequency.getMargin() : frequency.getMargin()));
		System.out.println("Current=" + current);
		ZonedDateTime previous = getNextIntakeDate(current, frequency);
		if (difference < 0) {
			previous = previous.minusHours(difference);
		} else {
			previous = previous.plusHours(difference);
		}
		final int start = previous.getHour();
		final List<Path> availablePaths = findAvailablePaths(min, difference, previous, arrival, frequency);
		if (availablePaths == null) {
			System.err.println("No path could be found.");
			return 0;
		}
		PathUtilities.setCosts(availablePaths, previous, arrival, frequency);
		final Path path = PathUtilities.getShortestPath(availablePaths);
		System.out.println("Demarcate[after=" + after + ", possibilities=" + availablePaths.size() + ", difference=" + difference + ", min_intake_moments=" + min + ", arrival=" + arrival.getHour() + ", start=" + start + ", paths=" + availablePaths.size() + ", path=" + path + "]");
		for(int i = 0; i < path.getSteps().length; i++) {
			final int step = path.getSteps()[i];
			if (i != 0 || !after) {
				previous = getNextIntakeDate(previous, frequency, after ? 1 : -1);
			}
			if (after) {
				previous = previous.withZoneSameLocal(arrival.getZone());
			}
			//System.out.println("Previous_first=" + previous + ", step=" + step);
			if (step < 0) {
				previous = previous.plusHours(step);
			} else {
				previous = previous.minusHours(step);
			}
			//System.out.println("Previous_second=" + previous + ", step=" + step);
			list.set(after ? (index + i + 1) : (index - i), new IntakeMoment(previous));
		}
		return min;
	}

	private static void writeIntakes(List<IntakeMoment> intakes) {
		IntakeMoment prevIntake = null;
		for (int i = 0; i < intakes.size(); i++) {
			IntakeMoment intake = intakes.get(i);
			if (prevIntake != null) {
				Duration difference = Duration.between(prevIntake.getDate(), intake.getDate());
				System.out.println(String.format("\t+%s", difference.toHours()));
			}
			System.out.println(String.format("[%d] %s", i, intake.getDate()));
			prevIntake = intake;
		}
	}

}
