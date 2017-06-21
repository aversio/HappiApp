package org.todss.algorithm.a.model;

import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;

import java.time.ZonedDateTime;

public class Travel {
    private Property<ZonedDateTime> departure = new SimpleObjectProperty<>();
    private Property<ZonedDateTime> arrival = new SimpleObjectProperty<>();

    public Travel(ZonedDateTime departure, ZonedDateTime arrival) {
        this.departure.setValue(departure);
        this.arrival.setValue(arrival);
    }

    public ZonedDateTime getDeparture() {
        return departure.getValue();
    }

    public void setDeparture(ZonedDateTime departure) {
        this.departure.setValue(departure);
    }

    public ZonedDateTime getArrival() {
        return arrival.getValue();
    }

    public void setArrival(ZonedDateTime arrival) {
        this.arrival.setValue(arrival);
    }

    public static Callback<Travel, Observable[]> extractor() {
        return (Travel t) -> new Observable[]{ t.departure, t.arrival };
    }

    @Override
    public String toString() {
        return "Travel{" +
                "departure=" + departure.getValue() +
                ", arrival=" + arrival.getValue() +
                '}';
    }
}
