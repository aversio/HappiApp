package org.todss.algorithm;

import org.todss.algorithm.path.Path;
import org.todss.algorithm.path.PathUtilities;
import org.todss.model.Alarm;
import org.todss.model.Frequency;
import org.todss.model.IntakeMoment;
import org.todss.model.Travel;

import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SmartAlgorithm {

	public static int MAX_INTAKE_MOMENTS = 3;

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

	public static void run(AlgorithmContext context) {
		final long start = System.currentTimeMillis();
		final ZonedDateTime[] range = getRange(context.getTravels());
		if (range == null) {
			return;
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
						//tijdsverschil is te groot, dus afbakenen of extra pil
						final int maxHours = frequency.getHours() / 2;
						if ((difference <= -maxHours || difference >= maxHours) || forceExtraIntake(context.getAlarm())) {
							//TODO Extra pil.
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
								currentZone = arrival.getZone();//TODO Fix deze
							}
							continue outer;
						}
					} else {
						//tijdsverschil valt binnen de marge
						current = current.minusHours(difference);
					}
					System.out.println("Traveling, difference=" + difference);
				} else if (current.getYear() == arrival.getYear() && current.getDayOfYear() == arrival.getDayOfYear()) {
					currentZone = arrival.getZone();
					current = current.withZoneSameLocal(currentZone);
				}
			}
			list.set(i, new IntakeMoment(current));
		}
		context.setIntakes(list);
		writeIntakes(list);
		System.out.println("Took " + (System.currentTimeMillis() - start) + " ms.");
	}

	public static ZonedDateTime getNextIntakeDate(ZonedDateTime current, Frequency frequency) {
		return getNextIntakeDate(current, frequency, 1);
	}

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
			//because the first day in summer time 02:00 doesn't exist, it go's to 03:00
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

	private static int demarcate(ZonedDateTime current, ZonedDateTime arrival, int difference, Frequency frequency, int index, List<IntakeMoment> list, boolean after) {
		final int min = (int) Math.ceil(difference / (double) (difference < 0 ? -frequency.getMargin() : frequency.getMargin()));
		ZonedDateTime previous = after ? current : getNextIntakeDate(current, frequency);
		if (difference < 0) {
			previous = previous.minusHours(difference);
		} else {
			previous = previous.plusHours(difference);
		}
		final int start = previous.getHour();
		List<Path> paths = PathUtilities.findPossiblePaths(min, difference);
		if (paths.size() == 0) {
			paths = PathUtilities.findPossiblePaths(MAX_INTAKE_MOMENTS, difference);
		}
		PathUtilities.setCosts(paths, previous, arrival, frequency);
		final List<Path> availablePaths = PathUtilities.findPathsAfterArrival(paths, previous, arrival, frequency);
		final Path path = PathUtilities.getShortestPath(availablePaths.size() == 0 ? paths : availablePaths);
		System.out.println("Demarcate[after=" + after + ", difference=" + difference + ", min_intake_moments=" + min + ", arrival=" + arrival.getHour() + ", start=" + start + ", paths=" + availablePaths.size() + ", path=" + Arrays.toString(path.getSteps()) + "]");
		for(int i = 0; i < path.getSteps().length; i++) {
			final int step = path.getSteps()[i];
			if (i != 0 || !after) {
				previous = getNextIntakeDate(previous, frequency, after ? 1 : -1);
			}
			if (after) {
				previous = previous.withZoneSameLocal(arrival.getZone());
			}
			if (step < 0) {
				previous = previous.plusHours(step);
			} else {
				previous = previous.minusHours(step);
			}
			System.out.println("Setting[index=" + (after ? (index + i) : (index - i)) + ", date=" + previous + "]");
			list.set(after ? (index + i) : (index - i), new IntakeMoment(previous));
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
