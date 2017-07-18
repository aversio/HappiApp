package org.todss.algorithm.model;

import java.util.List;

/**
 * An enumeration containing frequencies of medicines.
 * @author Displee
 * @author Jonahtan Peeman
 */
public enum Frequency {

	/**
	 * The half-day frequency.
	 */
    HALF_DAY(12, 2),

	/**
	 * The daily frequency.
	 */
    DAY(24, 4);

	/**
	 * The hours.
	 */
	private int hours;

	/**
	 * The maximum possible margin to take.
	 */
    private int margin;

    /**
     * Constructs a new {@code Frequency} {@code Object}.
     * @param hours The frequency in hours.
     * @param margin The margin in hours.
     */
    Frequency(int hours, int margin) {
        this.hours = hours;
        this.margin = margin;
    }

	/**
	 * Check if the argued hour {@code difference} is between the minimum and maximum margin range of this frequency.
	 * @param difference The difference in hours.
	 * @return If the difference is between the minimum and maximum margin range of this frequency.
	 */
	public boolean inRange(int difference) {
    	return difference >= (hours - margin) && difference <= (hours + margin);
	}

    /**
     * Get the frequency.
     * @return {@code hours}
     */
    public int getHours() {
        return hours;
    }

    /**
     * Get the margin.
     * @return {@code margin}
     */
    public int getMargin() {
    	return margin;
	}

    /**
     * Get the frequency in minutes.
     * @return The frequency in minutes.
     */
    public int getMinutes() {
        return hours * 60;
    }

}
