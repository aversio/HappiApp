package org.todss.algorithm.model;

import java.time.ZonedDateTime;

/**
 * A class representing a single intake moment.
 * @author Displee
 * @author Jonathan
 */
public class Intake {

	/**
	 * The intake date.
	 */
	private final ZonedDateTime date;

	/**
	 * Construct a new {@code Intake} {@code Object}.
	 * @param date The intake date.
	 */
	public Intake(ZonedDateTime date) {
		this.date = date;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Intake)) {
			return false;
		}
		Intake other = (Intake) obj;
		return date.isEqual(other.date);
	}

	/**
	 * Get the date.
 	 * @return {@code date}
	 */
	public ZonedDateTime getDate() {
		return date;
	}

	@Override
	public String toString() {
		return date.toString();
	}
}
