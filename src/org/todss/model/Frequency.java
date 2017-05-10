package org.todss.model;

public enum Frequency {
    HALF_DAY(12, Margin.HALF_DAY),
    DAY(24, Margin.DAILTY);

    private int hours;

    private Margin margin;

    Frequency(int hours, Margin margin) {
        this.hours = hours;
        this.margin = margin;
    }

    public int getHours() {
        return hours;
    }

    public Margin getMargin() {
    	return margin;
	}

    public int getOverdosesMargin() {
    	return hours / 2;
	}
}
