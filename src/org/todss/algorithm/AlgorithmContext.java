package org.todss.algorithm;

import org.todss.model.Alarm;
import org.todss.model.Travel;

import java.util.Arrays;
import java.util.List;

/**
 * A class serving as context holding the inputs to run our algorithm.
 * @author Displee
 */
public class AlgorithmContext {

	/**
	 * The alarm.
	 */
	private final Alarm alarm;

	/**
	 * A list of travels.
	 */
	private final List<Travel> travels;

	/**
	 * Construct a new {@code AlgorithmContext} {@code Object}.
	 * @param alarm The alarm.
	 * @param travels The travels.
	 */
	public AlgorithmContext(Alarm alarm, Travel... travels) {
		this(alarm, Arrays.asList(travels));
	}

	/**
	 * Construct a new {@code AlgorithmContext} {@code Object}.
	 * @param alarm The alarm.
	 * @param travels The travels.
	 */
	public AlgorithmContext(Alarm alarm, List<Travel> travels) {
		this.travels = travels;
		this.alarm = alarm;
	}

	/**
	 * Get the travels.
	 * @return {@code travels}
	 */
	public List<Travel> getTravels() {
		return travels;
	}

	/**
	 * Get the alarm.
	 * @return {@code alarm}
	 */
	public Alarm getAlarm() {
		return alarm;
	}

}
