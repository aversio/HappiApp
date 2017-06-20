package org.todss.model;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Travel {

    private ZonedDateTime departure;

    private ZonedDateTime arrival;

    private List<Travel> changes = new ArrayList<>();

    public Travel(ZonedDateTime departure, ZonedDateTime arrival) {
        this.departure = departure;
        this.arrival = arrival;
    }

    public void addChange(Travel travel) {
    	changes.add(travel);
	}

	public void addChange(ZonedDateTime departure, ZonedDateTime arrival) {
    	changes.add(new Travel(departure, arrival));
	}

	/**
	 * Get the hour difference between the departure and the arrival date.
	 * @return The difference in hours.
	 */
	public int getDifference() {
		final int arrivalOffset = departure.getZone().getRules().getOffset(arrival.toLocalDateTime()).getTotalSeconds() / 3600;
		final int departureOffset = arrival.getZone().getRules().getOffset(departure.toLocalDateTime()).getTotalSeconds() / 3600;
		return departureOffset - arrivalOffset;
	}

    public ZonedDateTime getDeparture() {
        return departure;
    }

    public void setDeparture(ZonedDateTime departure) {
        this.departure = departure;
    }

    public ZonedDateTime getArrival() {
        return arrival;
    }

    public void setArrival(ZonedDateTime arrival) {
        this.arrival = arrival;
    }

}
