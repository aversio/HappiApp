package org.todss.algorithm;

import org.todss.model.Alarm;
import org.todss.model.Frequency;
import org.todss.model.Travel;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AlgorithmTest {

	public static void main(String[] args) {
		List<Travel> travels = new ArrayList<>();
		travels.add(new Travel(ZonedDateTime.parse("2017-05-19T19:51+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam")), ZonedDateTime.parse("2017-05-20T19:51+00:00").withZoneSameLocal(ZoneId.of("America/Los_Angeles"))));
		travels.add(new Travel(ZonedDateTime.parse("2017-05-27T08:15+00:00").withZoneSameLocal(ZoneId.of("America/Los_Angeles")), ZonedDateTime.parse("2017-05-28T08:15+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam"))));
		AlgorithmContext context = new AlgorithmContext();
		context.setTravels(travels);
		context.setStart(ZonedDateTime.parse("2017-05-10T21:00+00:00"));
		context.setEnd(ZonedDateTime.parse("2017-05-31T21:00+00:00"));

		Alarm alarm = new Alarm(Frequency.DAY, ZonedDateTime.parse("2017-04-07T08:00+02:00", DateTimeFormatter.ISO_DATE_TIME));
		context.setAlarm(alarm);

		SmartAlgorithm.run(context);
	}

}
