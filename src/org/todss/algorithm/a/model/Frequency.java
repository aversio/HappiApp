package org.todss.algorithm.a.model;

public enum Frequency {
    HALF_DAY(12),
    DAY(24);

    private int hours;

    Frequency(int hours) {
        this.hours = hours;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return hours * 60;
    }
}
