package org.hu.happi.model;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public class Travel {
    private OffsetDateTime departure;
    private OffsetDateTime arrival;
    private ZoneId destinationZoneId;

    public Travel(OffsetDateTime departure, OffsetDateTime arrival, ZoneId destinationZoneId) {
        this.departure = departure;
        this.arrival = arrival;
        this.destinationZoneId = destinationZoneId;
    }

    public OffsetDateTime getDeparture() {
        return departure;
    }

    public void setDeparture(OffsetDateTime departure) {
        this.departure = departure;
    }

    public OffsetDateTime getArrival() {
        return arrival;
    }

    public void setArrival(OffsetDateTime arrival) {
        this.arrival = arrival;
    }

    public ZoneId getDestinationZoneId() {
        return destinationZoneId;
    }

    public void setDestinationZoneId(ZoneId destinationZoneId) {
        this.destinationZoneId = destinationZoneId;
    }
}
