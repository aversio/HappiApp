package test;

import org.junit.jupiter.api.Test;
import org.todss.algorithm.AlgorithmContext;
import org.todss.algorithm.SmartAlgorithm;
import org.todss.model.Alarm;
import org.todss.model.Frequency;
import org.todss.model.Travel;

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
	 * The alarm used to test our scenario's.
	 */
	private static final Alarm ALARM = new Alarm(Frequency.DAY, ZonedDateTime.parse("2017-04-07T08:00+00:00").withZoneSameLocal(ZoneId.of("Europe/Amsterdam")));

	/**
	 * Finally, test our algorithm.
	 * @param travels The travels.
	 */
	public void test(List<Travel> travels) {
		test(travels, ALARM);
	}

	/**
	 * Finally, test our algorithm.
	 * @param travels The travels.
	 * @param alarm The alarm.
	 */
	public void test(List<Travel> travels, Alarm alarm) {
		SmartAlgorithm.run(new AlgorithmContext(travels, alarm));
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
		test(travels);
	}

}
