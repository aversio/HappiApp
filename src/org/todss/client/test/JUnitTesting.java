package org.todss.client.test;

import org.junit.jupiter.api.Test;
import org.todss.algorithm.Algorithm;
import org.todss.algorithm.AlgorithmContext;
import org.todss.algorithm.impl.SmartAlgorithm;
import org.todss.algorithm.model.Alarm;
import org.todss.algorithm.model.Frequency;
import org.todss.algorithm.model.Intake;
import org.todss.algorithm.model.Travel;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A class used to test the functionality of our algorithm.
 * @author Displee
 */
public class JUnitTesting {

	/**
	 * If we have to write the intakes after a test.
	 */
	private static final boolean WRITE_INTAKES = true;

	/**
	 * The algorithm instance we use.
	 */
	private static final Algorithm ALGORITHM = new SmartAlgorithm();

	/**
	 * The alarm used to test our scenario's.
	 */
	private static final Alarm ALARM = new Alarm(Frequency.DAY, ZonedDateTime.parse("2017-04-07T08:00+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam")));

	/**
	 * Test the algorithm.
	 * @param travels The travels.
	 * @return A list of intake moments.
	 */
	private List<Intake> test(List<Travel> travels) {
		return test(ALARM, travels);
	}

	/**
	 * Test the algorithm.
	 * @param travels The travels.
	 * @param alarm The alarm.
	 * @return A list of intake moments.
	 */
	private List<Intake> test(Alarm alarm, List<Travel> travels) {
		return test(new AlgorithmContext(alarm, travels));
	}

	/**
	 * Finally, test our algorithm.
	 * @param context The algorithm context.
	 * @return A list of intake moments.
	 */
	private List<Intake> test(AlgorithmContext context) {
		final long start = System.currentTimeMillis();
		final List<Intake> list = ALGORITHM.run(context);
		System.out.println("Took " + (System.currentTimeMillis() - start) + " ms.");
		assert list != null && list.size() > 0;
		for(int i = 0; i < list.size(); i++) {
			final Intake current = list.get(i);
			if (i != 0) {
				final Intake previous = list.get(i - 1);
				final int difference = (int) Duration.between(previous.getDate(), current.getDate()).toHours();
				assert context.getAlarm().getFrequency().inRange(difference);
				if (WRITE_INTAKES) {
					System.out.println(String.format("\t+%s", difference));
				}
			}
			if (WRITE_INTAKES) {
				System.out.println(String.format("[%d] %s", i, current.getDate()));
			}
		}
		return list;
	}

	/**
	 * Test attributes:
	 * Journey:
	 * 		From: Europe/Amsterdam
	 * 		To: America/Los_Angeles
	 * 		Date category: summer time
	 * 		Departure time: morning
	 * 		Arrival time: morning
	 * Return trip:
	 * 		From: America/Los_Angeles
	 * 		To: Europe/Amsterdam
	 * 		Date category: summer time
	 * 		Departure time: afternoon
	 * 		Arrival time: morning
	 * Source: http://www.vliegtickets.nl/
	 */
	@Test
	public void test1() {
		List<Travel> travels = new ArrayList<>();
		Travel journey = new Travel(
				ZonedDateTime.parse("2017-06-16T10:55+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam")),
				ZonedDateTime.parse("2017-06-16T19:55+00:00").withZoneSameLocal(ZoneId.of("America/Los_Angeles"))
		);
		travels.add(journey);
		Travel returnTrip = new Travel(
				ZonedDateTime.parse("2017-06-29T13:55+00:00").withZoneSameLocal(ZoneId.of("America/Los_Angeles")),
				ZonedDateTime.parse("2017-06-30T09:15+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam"))
		);
		travels.add(returnTrip);
		final List<Intake> intakes = test(travels);
	}

	/**
	 * Test attributes:
	 * Journey:
	 * 		From: Europe/Amsterdam
	 * 		To: Asia/Tokyo
	 * 		Date category: summer time
	 * 		Departure time: afternoon
	 * 		Arrival time: morning
	 * Return trip:
	 * 		From: Asia/Tokyo
	 * 		To: Europe/Amsterdam
	 * 		Date category: summer time
	 * 		Departure time: morning
	 * 		Arrival time: afternoon
	 * Source: http://www.vliegtickets.nl/
	 */
	@Test
	public void test2() {
		List<Travel> travels = new ArrayList<>();
		Travel journey = new Travel(
				ZonedDateTime.parse("2017-06-23T14:40+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam")),
				ZonedDateTime.parse("2017-06-24T08:40+00:00").withZoneSameLocal(ZoneId.of("Asia/Tokyo"))
		);
		travels.add(journey);
		Travel returnTrip = new Travel(
				ZonedDateTime.parse("2017-06-30T10:30+00:00").withZoneSameLocal(ZoneId.of("Asia/Tokyo")),
				ZonedDateTime.parse("2017-06-30T15:10+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam"))
		);
		travels.add(returnTrip);
		final List<Intake> intakes = test(travels);
	}

	/**
	 * Test attributes:
	 * Journey:
	 * 		From: Europe/Amsterdam
	 * 		To: Asia/Bangkok
	 * 		Date category: summer time
	 * 		Departure time: afternoon
	 * 		Arrival time: morning
	 * Return trip:
	 * 		From: Asia/Bangkok
	 * 		To: Europe/Amsterdam
	 * 		Date category: winter time
	 * 		Departure time: afternoon
	 * 		Arrival time: evening
	 * Source: http://www.vliegtickets.nl/
	 */
	@Test
	public void test3() {
		List<Travel> travels = new ArrayList<>();
		Travel journey = new Travel(
				ZonedDateTime.parse("2017-10-27T17:50+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam")),
				ZonedDateTime.parse("2017-10-28T09:50+00:00").withZoneSameLocal(ZoneId.of("Asia/Bangkok"))
		);
		travels.add(journey);
		Travel returnTrip = new Travel(
				ZonedDateTime.parse("2017-11-06T12:15+00:00").withZoneSameLocal(ZoneId.of("Asia/Bangkok")),
				ZonedDateTime.parse("2017-11-06T18:30+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam"))
		);
		travels.add(returnTrip);
		final List<Intake> intakes = test(travels);
	}

	/**
	 * Test attributes:
	 * Journey:
	 * 		From: Europe/Amsterdam
	 * 		To: Africa/Casablanca
	 * 		Date category: summer time
	 * 		Departure time: evening
	 * 		Arrival time: evening
	 * Return trip:
	 * 		From: Africa/Casablanca
	 * 		To: Europe/Amsterdam
	 * 		Date category: summer time
	 * 		Departure time: evening
	 * 		Arrival time: afternoon
	 * Source: http://www.vliegtickets.nl/
	 */
	@Test
	public void test5() {
		List<Travel> travels = new ArrayList<>();
		Travel journey = new Travel(
				ZonedDateTime.parse("2017-06-29T17:50+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam")),
				ZonedDateTime.parse("2017-06-29T19:30+00:00").withZoneSameLocal(ZoneId.of("Africa/Casablanca"))
		);
		travels.add(journey);
		Travel returnTrip = new Travel(
				ZonedDateTime.parse("2017-06-29T23:40+00:00").withZoneSameLocal(ZoneId.of("Africa/Casablanca")),
				ZonedDateTime.parse("2017-06-30T14:45+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam"))
		);
		travels.add(returnTrip);
		final List<Intake> intakes = test(travels);
	}

	/**
	 * Test attributes:
	 * Journey:
	 * 		From: Europe/Amsterdam
	 * 		To: Europe/Istanbul
	 * 		Date category: summer time
	 * 		Departure time: afternoon
	 * 		Arrival time: evening
	 * Return trip:
	 * 		From: Europe/Istanbul
	 * 		To: Europe/Amsterdam
	 * 		Date category: summer time
	 * 		Departure time: evening
	 * 		Arrival time: evening
	 * Source: http://www.vliegtickets.nl/
	 */
	@Test
	public void test6() {
		List<Travel> travels = new ArrayList<>();
		Travel journey = new Travel(
				ZonedDateTime.parse("2017-06-16T15:00+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam")),
				ZonedDateTime.parse("2017-07-16T19:50+00:00").withZoneSameLocal(ZoneId.of("Europe/Istanbul"))
		);
		travels.add(journey);
		Travel returnTrip = new Travel(
				ZonedDateTime.parse("2017-07-17T19:15+00:00").withZoneSameLocal(ZoneId.of("Europe/Istanbul")),
				ZonedDateTime.parse("2017-07-17T22:15+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam"))
		);
		travels.add(returnTrip);
		final List<Intake> intakes = test(travels);
	}

	/**
	 * Test attributes:
	 * Journey:
	 * 		From: Europe/Amsterdam
	 * 		To: Asia/Kuala_Lumpur
	 * 		Date category: summer time
	 * 		Departure time: afternoon
	 * 		Arrival time: morning
	 * Return trip:
	 * 		From: Asia/Kuala_Lumpur
	 * 		To: Europe/Amsterdam
	 * 		Date category: summer time
	 * 		Departure time: evening
	 * 		Arrival time: morning
	 * Source: http://www.vliegtickets.nl/
	 */
	@Test
	public void test7() {
		List<Travel> travels = new ArrayList<>();
		Travel journey = new Travel(
				ZonedDateTime.parse("2017-08-11T12:50+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam")),
				ZonedDateTime.parse("2017-08-12T06:50+00:00").withZoneSameLocal(ZoneId.of("Asia/Kuala_Lumpur"))
		);
		travels.add(journey);
		Travel returnTrip = new Travel(
				ZonedDateTime.parse("2017-08-17T23:20+00:00").withZoneSameLocal(ZoneId.of("Asia/Kuala_Lumpur")),
				ZonedDateTime.parse("2017-08-18T06:00+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam"))
		);
		travels.add(returnTrip);
		final List<Intake> intakes = test(travels);
	}

	/**
	 * Test attributes:
	 * Test id =  14 & 20
	 * Test name: Travel with 2 changes & Travel to a countrey with MAX time difference
	 * Journey:
	 * 		From: Europe/Amsterdam
	 * 		To: Asia/Anadyr
	 * 		Date category: winter time
	 * 		Departure time: night
	 * 		Arrival time: afternoon
	 * Return trip:
	 * 		From: Asia/Anadyr
	 * 		To: Europe/Amsterdam
	 * 		Date category: winter time
	 * 		Departure time: evening
	 * 		Arrival time: afternoon
	 * Source: http://www.vliegtickets.nl/
	 */
	@Test
	public void test14() {
		List<Travel> travels = new ArrayList<>();
		Travel journey = new Travel(
				ZonedDateTime.parse("2017-08-21T00:15+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam")),
				ZonedDateTime.parse("2017-08-22T14:50+00:00").withZoneSameLocal(ZoneId.of("Asia/Anadyr"))
		);
		travels.add(journey);
		Travel returnTrip = new Travel(
				ZonedDateTime.parse("2017-08-29T23:15+00:00").withZoneSameLocal(ZoneId.of("Asia/Anadyr")),
				ZonedDateTime.parse("2017-08-30T13:50+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam"))
		);
		travels.add(returnTrip);
		final List<Intake> intakes = test(travels);
	}

	/**
	 * Test attributes:
	 * Test id =  22 & 16
	 * Test name: Travel to a countrey with MIN time difference & Travel with a length of 3 days.
	 * Journey:
	 * 		From: Europe/Amsterdam
	 * 		To: Asia/Anadyr
	 * 		Date category: winter time
	 * 		Departure time: afternoon
	 * 		Arrival time: afternoon
	 * Return trip:
	 * 		From: Asia/Anadyr
	 * 		To: Europe/Amsterdam
	 * 		Date category: winter time
	 * 		Departure time: evening
	 * 		Arrival time: evening
	 * Source: http://www.vliegtickets.nl/
	 */
	@Test
	public void test22() {
		List<Travel> travels = new ArrayList<>();
		Travel journey = new Travel(
				ZonedDateTime.parse("2017-09-21T13:30+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam")),
				ZonedDateTime.parse("2017-09-21T14:40+00:00").withZoneSameLocal(ZoneId.of("Europe/Brussels"))
		);
		travels.add(journey);
		Travel returnTrip = new Travel(
				ZonedDateTime.parse("2017-09-24T20:15+00:00").withZoneSameLocal(ZoneId.of("Europe/Brussels")),
				ZonedDateTime.parse("2017-09-24T21:25+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam"))
		);
		travels.add(returnTrip);
		final List<Intake> intakes = test(travels);
	}

	/**
	 * Test attributes:
	 * Test id =  21
	 * Test name: Travel to a countrey with AVG time difference
	 * Journey:
	 * 		From: Europe/Amsterdam
	 * 		To: Asia/Tehera
	 * 		Date category: winter time
	 * 		Departure time: night
	 * 		Arrival time: afternoon
	 * Return trip:
	 * 		From: Asia/Tehera
	 * 		To: Europe/Amsterdam
	 * 		Date category: winter time
	 * 		Departure time: evening
	 * 		Arrival time: afternoon
	 * Source: http://www.vliegtickets.nl/
	 */
	@Test
	public void tes21(){
		List<Travel> travels = new ArrayList<>();
		Travel jouney = new Travel(
				ZonedDateTime.parse("2017-08-21T17:40+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam")),
				ZonedDateTime.parse("2017-08-22T01:20+00:00").withZoneSameLocal(ZoneId.of("Asia/Tehran"))
		);
		travels.add(jouney);
		Travel returnTrip = new Travel(
				ZonedDateTime.parse("2017-08-25T09:00+00:00").withZoneSameLocal(ZoneId.of("Asia/Tehran")),
				ZonedDateTime.parse("2017-08-25T17:20+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam"))
		);
		travels.add(returnTrip);
		final List<Intake> intakes = test(travels);
	}

	/**
	 * Test attributes:
	 * Test id =  23
	 * Test name: Travel with 1 schema to 2 different locations
	 * firstJourney:
	 * 		From: Europe/Amsterdam
	 * 		To: Europe/Brussels
	 * 		Date category: summer time
	 * 		Departure time: afternoon
	 * 		Arrival time: evening
	 * secondJourney:
	 * 		From: Europe/Brussels
	 * 		To: Europe/Berlin
	 * 		Date category: summer time
	 * 		Departure time: morning
	 * 		Arrival time: morning
	 * Return trip:
	 * 		From: Europe/Berlin
	 * 		To: Europe/Amsterdam
	 * 		Date category: summer time
	 * 		Departure time: morning
	 * 		Arrival time: morning
	 * Source: http://www.vliegtickets.nl/
	 */
	@Test
	public void tes23(){
		List<Travel> travels = new ArrayList<>();
		Travel firstJourney = new Travel(
				ZonedDateTime.parse("2017-03-21T17:40+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam")),
				ZonedDateTime.parse("2017-03-21T18:40+00:00").withZoneSameLocal(ZoneId.of("Europe/Brussels"))
		);
		travels.add(firstJourney);
		Travel secondJourney = new Travel(
				ZonedDateTime.parse("2017-03-24T09:00+00:00").withZoneSameLocal(ZoneId.of("Europe/Brussels")),
				ZonedDateTime.parse("2017-03-24T11:20+00:00").withZoneSameLocal(ZoneId.of("Europe/Berlin"))
		);
		travels.add(secondJourney);
		Travel returnTrip = new Travel(
				ZonedDateTime.parse("2017-03-25T09:00+00:00").withZoneSameLocal(ZoneId.of("Europe/Berlin")),
				ZonedDateTime.parse("2017-03-25T10:20+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam"))
		);
		travels.add(returnTrip);
		final List<Intake> intakes = test(travels);
	}

}