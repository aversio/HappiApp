package org.todss.model;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Alarm {
    private Frequency frequency;

    private ZonedDateTime start;

    public Alarm(Frequency frequency, ZonedDateTime start) {
        this.frequency = frequency;
        this.start = start;
    }

	public List<ZonedDateTime> getScheme(int weeks) {
    	return getScheme(weeks, null, null, null);
	}

    public List<ZonedDateTime> getScheme(int weeks, ZonedDateTime start, ZonedDateTime from, ZoneId zoneId) {
    	if (start == null) {
    		start = this.start;
		}
    	List<ZonedDateTime> list = new ArrayList<>();
    	int days = weeks * 7;
    	for(int i = 1; i <= days; i++) {
			ZonedDateTime date = start.plusDays(i);
    		if (from != null && date.compareTo(from) == 1) {
    			date = date.withZoneSameInstant(zoneId);
				//date = date.atZoneSameInstant(zoneId).toOffsetDateTime();
			}
    		list.add(date);
		}
    	return list;
	}

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

}
