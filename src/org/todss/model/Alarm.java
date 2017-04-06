package org.hu.happi.model;

import java.time.OffsetDateTime;

public class Alarm {
    private Frequency frequency;
    private OffsetDateTime start;

    public Alarm(Frequency frequency, OffsetDateTime start) {
        this.frequency = frequency;
        this.start = start;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public OffsetDateTime getStart() {
        return start;
    }

    public void setStart(OffsetDateTime start) {
        this.start = start;
    }
}
