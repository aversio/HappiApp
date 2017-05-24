package org.todss.algorithm;

import org.todss.model.Alarm;
import org.todss.model.IntakeMoment;
import org.todss.model.Travel;

import java.time.ZonedDateTime;
import java.util.List;

public class AlgorithmContext {

	private ZonedDateTime start;

	private ZonedDateTime end;

	private Alarm alarm;

	private List<Travel> travels;

	private List<IntakeMoment> intakes;

	public ZonedDateTime getStart() {
		return start;
	}

	public void setStart(ZonedDateTime start) {
		this.start = start;
	}

	public ZonedDateTime getEnd() {
		return end;
	}

	public void setEnd(ZonedDateTime end) {
		this.end = end;
	}

	public List<Travel> getTravels() {
		return travels;
	}

	public void setTravels(List<Travel> travels) {
		this.travels = travels;
	}

	public Alarm getAlarm() {
		return alarm;
	}

	public void setAlarm(Alarm alarm) {
		this.alarm = alarm;
	}

	public List<IntakeMoment> getIntakes() {
		return intakes;
	}

	public void setIntakes(List<IntakeMoment> intakes) {
		this.intakes = intakes;
	}
}
