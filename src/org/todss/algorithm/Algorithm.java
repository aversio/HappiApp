package org.todss.algorithm;

import org.todss.model.Alarm;
import org.todss.model.IntakeMoment;
import org.todss.model.Travel;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Algorithm {

	public static final ZoneId AMSTERDAM = ZoneId.of("Europe/Amsterdam");

	public static final int MAX_INTAKE_DAYS = 3;

	public void run(AlgorithmContext context) {
		final long daysLength = Duration.between(context.getStart(), context.getEnd()).toDays();
		final List<IntakeMoment> list = new ArrayList<>();
		for(int i = 0; i < daysLength; i++) {
			list.add(null);
		}
		ZoneId zone = AMSTERDAM;
		outer: for(int i = 1; i <= daysLength; i++) {
			ZonedDateTime date = context.getStart().plusDays(i).withZoneSameLocal(zone).withHour(context.getAlarm().getStart().getHour());
			for(Travel travel : context.getTravels()) {
				final ZonedDateTime departure = travel.getDeparture();
				final ZonedDateTime arrival = travel.getArrival();
				if (date.getYear() == departure.getYear() && date.getDayOfYear() == departure.getDayOfYear()) {
					final int difference = calculateDifference(departure, arrival);
					final int margin = context.getAlarm().getFrequency().getMargin().getHours();
					//TODO Support for minutes and maybe seconds.
					if (difference < -margin || difference > margin) {
						//tijdsverschil is te groot, dus afbakenen of extra pil
						//TODO Support for minutes and maybe seconds.
						final int maxHours = margin * MAX_INTAKE_DAYS;
						if ((difference <= -maxHours || difference >= maxHours) || checkProperties(context.getAlarm())) {
							//extra pil
							//TODO Extra pil
						} else {
							if (difference > 0) {
								//vooraf afbakenen
								demarcateBefore(date, i, margin, difference, list);
							} else {
								//achteraf afbakenen
								i += demarcateAfterwards(date, arrival, i, margin, difference, list);
							}
							continue outer;
						}
					} else {
						//TODO Slapen valt hier atm ook onder, is dat goed?
						//tijdsverschil valt binnen de marge
						//TODO Support for minutes and maybe seconds.
						date = date.minusHours(difference);
					}
				}
				if (date.getYear() == arrival.getYear() && date.getDayOfYear() == arrival.getDayOfYear()) {
					zone = arrival.getZone();
					date = date.withZoneSameLocal(arrival.getZone());
				}
			}
			list.set(i - 1, new IntakeMoment(date));
		}
		context.setIntakes(list);
		for(IntakeMoment intake : list) {
			System.out.println(intake);
		}
	}

	private boolean checkProperties(Alarm alarm) {
		if ((boolean) alarm.getProperty("sleeping")) {
			return true;
		}
		return false;
	}

	//TODO Support for minutes and maybe seconds.
	private int calculateDifference(ZonedDateTime departure, ZonedDateTime arrival) {
		final int arrivalOffset = departure.getZone().getRules().getOffset(arrival.toLocalDateTime()).getTotalSeconds() / 3600;
		final int departureOffset = arrival.getZone().getRules().getOffset(departure.toLocalDateTime()).getTotalSeconds() / 3600;
		return departureOffset - arrivalOffset;
	}

	private int calculateOverflow(ZonedDateTime departure, ZonedDateTime arrival, List<IntakeMoment> list) {
		int overflow = arrival.getDayOfYear() - departure.getDayOfYear();
		for(int i = 0; i < overflow; i++) {
			list.add(new IntakeMoment(departure.withZoneSameLocal(ZoneId.of("Europe/Paris"))));
		}
		return overflow;
	}

	public void demarcateBefore(ZonedDateTime date, int i, int margin, int difference, List<IntakeMoment> list) {
		int days = (int) Math.ceil((double) (difference < 0 ? -difference : difference) / margin);
		int completedDifference = 0;
		int counter = days;
		//System.out.println("Difference=" + difference + ", dagen om af te bakenen=" + days);
		while (completedDifference != difference) {
			int left = difference - completedDifference;
			if (left < -margin || left > margin) {
				left = (difference < 0 ? -margin : margin);
			}
			completedDifference += left;
			final int index = i - counter;
			//System.out.println("left=" + left + ", completed=" + completedDifference + ", i=" + i + ", index=" + index);
			if (index == i - 1) {
				if (left < 0) {
					date = date.plusHours(completedDifference);
				} else {
					date = date.minusHours(completedDifference);
				}
				list.set(index, new IntakeMoment(date));
			} else {
				final IntakeMoment intake = list.get(index);
				final ZonedDateTime previousDay = intake.getDate();
				if (left < 0) {
					intake.setDate(previousDay.plusHours(completedDifference));
				} else {
					intake.setDate(previousDay.minusHours(completedDifference));
				}
			}
			counter--;
		}
	}

	private int demarcateAfterwards(ZonedDateTime date, ZonedDateTime arrival, int i, int margin, int difference, List<IntakeMoment> list) {
		int days = (int) Math.ceil((double) (difference < 0 ? -difference : difference) / margin);
		int completedDifference = 0;
		int counter = days;
		System.out.println("Arrival=" + arrival);
		System.out.println("Difference=" + difference + ", dagen om af te bakenen=" + days);
		//TODO Calculate overflow?
		//list.set(i - 1, new IntakeMoment(arrival));
		while (completedDifference != difference) {
			int left = difference - completedDifference;
			if (left < -margin || left > margin) {
				left = (difference < 0 ? -margin : margin);
			}
			completedDifference += left;
			final int index = i + counter - 1;
			ZonedDateTime newDate = arrival.plusDays(days - counter);
			System.out.println("left=" + left + ", completed=" + completedDifference + ", i=" + i + ", index=" + index);
			if (index == i) {
				if (left < 0) {
					date = newDate.plusHours(completedDifference);
				} else {
					date = newDate.minusHours(completedDifference);
				}
				list.set(index, new IntakeMoment(date));
			} else {
				if (left < 0) {
					list.set(index, new IntakeMoment(newDate.plusHours(completedDifference)));
				} else {
					list.set(index, new IntakeMoment(newDate.minusHours(completedDifference)));
				}
			}
			System.out.println("setting new moment=" + list.get(index));
			counter--;
		}
		return 0;//calculateOverflow(date, arrival, list) + days;
	}

}
