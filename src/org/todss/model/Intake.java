package org.todss.model;

import java.time.ZonedDateTime;

public class Intake {
    ZonedDateTime date;

    public Intake(ZonedDateTime date) {
        this.date = date;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Intake{" +
                "date=" + date +
                '}';
    }
}
