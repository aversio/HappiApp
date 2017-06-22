package org.todss.client.test.model;

import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;
import org.todss.algorithm.model.Travel;

import java.time.ZonedDateTime;

public class FXTravel {
    private SimpleObjectProperty<ZonedDateTime> departure = new SimpleObjectProperty<>();
    private SimpleObjectProperty<ZonedDateTime> arrival = new SimpleObjectProperty<>();

    public FXTravel(ZonedDateTime departure, ZonedDateTime arrival) {
        this.departure.set(departure);
        this.arrival.set(arrival);
    }

    public static Callback<FXTravel, Observable[]> extractor() {
        return (FXTravel t) -> new Observable[]{ t.departure, t.arrival };
    }

    public ZonedDateTime getDeparture() {
        return departure.get();
    }

    public void setDeparture(ZonedDateTime departure) {
        this.departure.set(departure);
    }

    public ZonedDateTime getArrival() {
        return arrival.get();
    }

    public void setArrival(ZonedDateTime arrival) {
        this.arrival.set(arrival);
    }

    public void setTravel(ZonedDateTime departure, ZonedDateTime arrival) {
		this.departure.set(departure);
		this.arrival.set(arrival);
    }

    public Travel toTravel() {
        return new Travel(departure.get(), arrival.get());
    }

	@Override
	public String toString() {
		return "FXTravel{" + "departure=" + departure + ", arrival=" + arrival + '}';
	}
}