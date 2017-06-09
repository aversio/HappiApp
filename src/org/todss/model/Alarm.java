package org.todss.model;

import java.time.ZonedDateTime;

public class Alarm {
    private Frequency frequency;
    private ZonedDateTime start;
    private int margin;

    public Alarm(Frequency frequency, ZonedDateTime start, int margin) {
        this.frequency = frequency;
        this.start = start;
        this.margin = margin;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }
}
