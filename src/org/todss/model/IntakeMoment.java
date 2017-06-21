package org.todss.model;

import java.time.ZonedDateTime;

/**
 * A class representing a single intake moment.
 * @author Displee
 * @author Jonathan
 */
public class IntakeMoment {

	/**
	 * The intake date.
	 */
	private final ZonedDateTime date;

	/**
	 * Construct a new {@code IntakeMoment} {@code Object}.
	 * @param date The intake date.
	 */
	public IntakeMoment(ZonedDateTime date) {
		this.date = date;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IntakeMoment)) {
			return false;
		}
		IntakeMoment other = (IntakeMoment) obj;
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
