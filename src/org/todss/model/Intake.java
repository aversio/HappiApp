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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Intake intake = (Intake) o;

        return date != null ? date.equals(intake.date) : intake.date == null;
    }

    @Override
    public int hashCode() {
        return date != null ? date.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Intake{" +
                "date=" + date +
                '}';
    }
}
