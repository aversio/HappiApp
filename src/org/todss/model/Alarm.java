package org.todss.model;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Alarm {

    private Frequency frequency;

    private ZonedDateTime start;

    private Properties properties = new Properties();

    public Alarm(Frequency frequency, ZonedDateTime start) {
        this.frequency = frequency;
        this.start = start;
        properties.put("sleeping", false);
    }

    public Object getProperty(String key) {
    	return properties.get(key);
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
		boolean updated = false;
    	for(int i = 1; i <= days; i++) {
			ZonedDateTime date = start.plusDays(i);
    		if (from != null && date.compareTo(from) == 1) {
    			if (!updated) {
					updated = true;
					ZonedDateTime newDate = date.withZoneSameInstant(zoneId);
					int difference = newDate.getHour() - date.getHour();
					int margin = frequency.getMargin().getHours();
					if (difference < -margin || difference > margin) {
						int daysBefore = (int) Math.ceil((double) (difference < 0 ? -difference : difference) / frequency.getMargin().getHours());
						int completedDifference = 0;
						int counter = daysBefore;
						System.out.println("Difference=" + difference + ", dagen om af te bakenen=" + daysBefore);
						while (completedDifference < difference) {
							int left = difference - completedDifference;
							if (left > margin) {
								left = margin;
							}
							int index = (i - 1) - counter;
							ZonedDateTime previousDay = list.get(index);
							list.set(index, previousDay.minusHours(left));
							System.out.println(previousDay + " min " + left + " uur.");
							completedDifference += left;
							counter--;
						}
					}
				}
				date = date.withZoneSameLocal(zoneId);
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
