package org.todss.algorithm;

import com.sun.scenario.animation.AbstractMasterTimer;
import org.todss.model.Alarm;
import org.todss.model.Frequency;
import org.todss.model.IntakeMoment;
import org.todss.model.Travel;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.todss.algorithm.Algorithm.AMSTERDAM;

public class SmartAlgorithm {

	public static void run(AlgorithmContext context) {
		final long daysLength = Duration.between(context.getStart(), context.getEnd()).toDays();
		final Frequency frequency = context.getAlarm().getFrequency();
		final List<IntakeMoment> list = new ArrayList<>();
		final int length = (int) daysLength * (frequency.getHours() == 24 ? 1 : 2);
		System.out.println("Settings[freq=" + frequency.getHours() + ", intakes_length=" + length + ", period=" + daysLength + "]");
		for(int i = 0; i < length; i++) {
			list.add(null);
		}
		ZoneId currentZone = AMSTERDAM;
		outer: for(int i = 0; i < length; i++) {
			ZonedDateTime current = getNext(context.getStart(), frequency, i).withZoneSameLocal(currentZone).withHour(context.getAlarm().getStart().getHour());
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
							ZonedDateTime temp = difference < 0 ? current.plusHours(difference) : current.minusHours(difference);
							System.out.println("Temp hour=" + temp.getHour());
							//TODO Support voor vroege vogels
							if (temp.getHour() > 22 || temp.getHour() < 8) {
								//Achteraf
								list.set(i, new IntakeMoment(current));
								i += demarcateAfter(current, arrival, margin, difference, frequency, i, list);
								currentZone = arrival.getZone();//TODO Fix deze
								continue outer;
							} else {
								//Vooraf
								demarcateBefore(current, margin, difference, frequency, i, list);
								continue outer;
							}
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
	}

	private static ZonedDateTime getNext(ZonedDateTime current, Frequency frequency) {
		return getNext(current, frequency, 0);
	}

	private static ZonedDateTime getNext(ZonedDateTime current, Frequency frequency, int amount) {
		//TODO Support for winter and summer time.
		final int hours = frequency.getHours();
		if (frequency == Frequency.HALF_DAY) {
			return current.plusHours(amount == 0 ? hours : (amount * hours));
		} else {
			return current.plusDays(amount == 0 ? 1 : amount);
		}
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

	private static void demarcateBefore(ZonedDateTime current, int margin, int difference, Frequency frequency, int index, List<IntakeMoment> list) {
		final int days = (int) Math.ceil((double) (difference < 0 ? -difference : difference) / margin);
		ZonedDateTime previous = getNext(current, frequency).minusHours(difference);
		int completed = 0;
		int[] path = new int[days];
		for(int i = 0; i < days; i++) {
			int left = difference - completed;
			if (left > margin || left < -margin) {
				left = difference < 0 ? -margin : margin;
			}
			list.set(index - i, new IntakeMoment(previous = getNext(previous, frequency, -1).plusHours(left)));
			completed += left;
			path[i] = left;
		}
		System.out.println("Demarcate before[difference=" + difference + ", days=" + days + ", start=" + previous.getHour() + ", path=" + Arrays.toString(path) + "]");
	}

	private static int demarcateAfter(ZonedDateTime current, ZonedDateTime arrival, int margin, int difference, Frequency frequency, int index, List<IntakeMoment> list) {
		final int days = (int) Math.ceil((double) (difference < 0 ? -difference : difference) / margin);
		final int start = current.minusHours(difference).getHour();
		int completed = 0;
		ZonedDateTime previous = current.minusHours(difference);
		int[] path = new int[days];
		for(int i = 1; i <= days; i++) {
			int left = difference - completed;
			if (left > margin || left < -margin) {
				left = difference < 0 ? -margin : margin;
			}
			list.set(index + i, new IntakeMoment(previous = getNext(previous, frequency).withZoneSameLocal(arrival.getZone()).plusHours(left)));
			completed += left;
			path[i - 1] = left;
		}
		System.out.println("Demarcate after[difference=" + difference + ", days=" + days + ", arrival=" + arrival.getHour() + ", start=" + start + ", path=" + Arrays.toString(path) + "]");
		return days;
	}

	private static void writeIntakes(List<IntakeMoment> intakes) {
		IntakeMoment prevIntake = null;
		for (int i = 0; i < intakes.size(); i++) {
			IntakeMoment intake = intakes.get(i);

			if (prevIntake != null) {
				Duration difference = Duration.between(prevIntake.getDate(), intake.getDate());

				System.out.println(
						String.format("\t+%s", difference.toHours())
				);
			}

			System.out.println(String.format("[%d] %s", i, intake.getDate()));

			prevIntake = intake;
		}
	}

}
