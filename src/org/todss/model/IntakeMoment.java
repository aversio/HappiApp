package org.todss.model;

import java.time.ZonedDateTime;

public class IntakeMoment {

	private ZonedDateTime date;

	public IntakeMoment(ZonedDateTime date) {
		this.date = date;
	}

	public void setDate(ZonedDateTime date) {
		this.date = date;
	}

	public ZonedDateTime getDate() {
		return date;
	}

	@Override
	public String toString() {
		return date.toString();
	}

}
