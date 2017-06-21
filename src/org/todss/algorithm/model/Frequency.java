package org.todss.algorithm.model;

/**
 * Frequency
 */
public enum Frequency {
    HALF_DAY(12, 2),
    DAY(24, 4);

    private int hours;
    private int margin;

    /**
     * Frecuency for an alarm.
     * @param hours the frequency of the alarm
     * @param margin margin of the alarm
     */
    Frequency(int hours, int margin) {
        this.hours = hours;
        this.margin = margin;
    }

    /**
     * Get the frequency.
     * @return frequency in hours
     */
    public int getHours() {
        return hours;
    }

    /**
     * Get the margin.
     * @return margin in hours
     */
    public int getMargin() {
    	return margin;
	}

    /**
     * Get the margin.
     * @return margin in minutes
     */
    public int getMinutes() {
        return hours * 60;
    }
}
