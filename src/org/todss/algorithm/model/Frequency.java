package org.todss.algorithm.model;

public enum Frequency {
    HALF_DAY(12, 2),
    DAY(24, 4);

    private int hours;

    private int margin;

    Frequency(int hours, int margin) {
        this.hours = hours;
        this.margin = margin;
    }

    public int getHours() {
        return hours;
    }

    public int getMargin() {
    	return margin;
	}

    public int getMinutes() {
        return hours * 60;
    }
}
