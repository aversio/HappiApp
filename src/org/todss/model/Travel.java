package org.todss.model;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Travel {

    private ZonedDateTime departure;

    private ZonedDateTime arrival;

    public Travel(ZonedDateTime departure, ZonedDateTime arrival) {
        this.departure = departure;
        this.arrival = arrival;
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
