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
