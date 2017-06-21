package test;

import org.junit.jupiter.api.Test;
import org.todss.algorithm.Algorithm;
import org.todss.algorithm.AlgorithmContext;
import org.todss.algorithm.impl.SmartAlgorithm;
import org.todss.model.Alarm;
import org.todss.model.Frequency;
import org.todss.model.IntakeMoment;
import org.todss.model.Travel;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A class used to test the functionality of our algorithm.
 * @author Displee
 */
public class AlgorithmTest {

	/**
	 * If we have to write the intakes after a test.
	 */
	private static final boolean WRITE_INTAKES = false;

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
	private List<IntakeMoment> test(List<Travel> travels) {
		return test(ALARM, travels);
	}

	/**
	 * Test the algorithm.
	 * @param travels The travels.
	 * @param alarm The alarm.
	 * @return A list of intake moments.
	 */
	private List<IntakeMoment> test(Alarm alarm, List<Travel> travels) {
		return test(new AlgorithmContext(alarm, travels));
	}

	/**
	 * Finally, test our algorithm.
	 * @param context The algorithm context.
	 * @return A list of intake moments.
	 */
	private List<IntakeMoment> test(AlgorithmContext context) {
		final long start = System.currentTimeMillis();
		final List<IntakeMoment> list = ALGORITHM.run(context);
		System.out.println("Took " + (System.currentTimeMillis() - start) + " ms.");
		assert list != null && list.size() > 0;
		if (WRITE_INTAKES) {
			writeIntakes(list);
		}
		return list;
	}

	/**
	 * Write a list of intake moments in a fancy format.
	 * @param intakes The list of intakes to write.
	 */
	private void writeIntakes(List<IntakeMoment> intakes) {
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
				ZonedDateTime.parse("2017-06-16T11:55+00:00").withZoneSameLocal(ZoneId.of("America/Los_Angeles"))
		);
		travels.add(journey);
		Travel returnTrip = new Travel(
				ZonedDateTime.parse("2017-06-29T13:55+00:00").withZoneSameLocal(ZoneId.of("America/Los_Angeles")),
				ZonedDateTime.parse("2017-06-30T09:15+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam"))
		);
		travels.add(returnTrip);
		final List<IntakeMoment> intakes = test(travels);
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
		final List<IntakeMoment> intakes = test(travels);
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
		final List<IntakeMoment> intakes = test(travels);
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
		final List<IntakeMoment> intakes = test(travels);
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
		final List<IntakeMoment> intakes = test(travels);
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
		final List<IntakeMoment> intakes = test(travels);
	}

}
