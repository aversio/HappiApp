package org.todss.algorithm;

import org.todss.model.Alarm;
import org.todss.model.IntakeMoment;
import org.todss.model.Travel;

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
	 * A list of intake moments.
	 */
	private List<IntakeMoment> intakes;

	/**
	 * Construct a new {@code AlgorithmContext} {@code Object}.
	 * @param travels The travels.
	 * @param alarm The alarm.
	 */
	public AlgorithmContext(List<Travel> travels, Alarm alarm) {
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

	/**
	 * Get the generated intake moments of this context.
	 * @return {@code intakes}
	 */
	public List<IntakeMoment> getIntakes() {
		return intakes;
	}

	/**
	 * Set a new list of intake moments for this context.
	 * @param intakes The new list of intake moments to set.
	 */
	public void setIntakes(List<IntakeMoment> intakes) {
		this.intakes = intakes;
	}

}
