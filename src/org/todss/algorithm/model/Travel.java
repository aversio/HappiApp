package org.todss.algorithm.model;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a travel.
 * @author Displee
 * @author Jonathan
 */
public class Travel {

	/**
	 * The departure.
	 */
    private ZonedDateTime departure;

	/**
	 * The arrival.
	 */
	private ZonedDateTime arrival;

	/**
	 * The changes that will occur between the departure and arrival.
	 */
    private final List<Travel> changes = new ArrayList<>();

	/**
	 * Create a new {@code Travel} {@code Object}.
	 * @param departure The departure.
	 * @param arrival The arrival.
	 */
	public Travel(ZonedDateTime departure, ZonedDateTime arrival) {
        this.departure = departure;
        this.arrival = arrival;
    }

	/**
	 * Add a change.
	 * @param departure The departure date.
	 * @param arrival The arrival date.
	 */
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

	/**
	 * Get the departure.
	 * @return {@code departure}
	 */
    public ZonedDateTime getDeparture() {
        return departure;
    }

	/**
	 * Get the arrival.
	 * @return {@code arrival}
	 */
	public ZonedDateTime getArrival() {
        return arrival;
    }
}
