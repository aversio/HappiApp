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
	private Alarm alarm;

	/**
	 * A list of travels.
	 */
	private List<Travel> travels;

	/**
	 * A list of intake moments.
	 */
	private List<IntakeMoment> intakes;

	/**
	 * Get the travels.
	 * @return {@code travels}
	 */
	public List<Travel> getTravels() {
		return travels;
	}

	/**
	 * Set the travels for this context.
	 * @param travels The new list of travels to set.
	 */
	public void setTravels(List<Travel> travels) {
		this.travels = travels;
	}

	/**
	 * Get the alarm.
	 * @return {@code alarm}
	 */
	public Alarm getAlarm() {
		return alarm;
	}

	/**
	 * Set a new alarm for this context.
	 * @param alarm The new alarm to set.
	 */
	public void setAlarm(Alarm alarm) {
		this.alarm = alarm;
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
