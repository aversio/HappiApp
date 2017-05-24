package org.todss.model;

public enum Margin {

	HALF_DAY(2),

	DAILTY(4);

	private final int hours;

	Margin(int hours) {
		this.hours = hours;
	}

	public int getHours() {
		return hours;
	}

}
