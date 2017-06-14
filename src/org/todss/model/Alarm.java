package org.todss.model;

import java.time.ZonedDateTime;
import java.util.Properties;

/**
 * A class representing an alarm used to remind you.
 * @author Displee
 */
public class Alarm {

	/**
	 * The default properties.
	 */
	private static final Properties DEFAULT_PROPERTIES = new Properties();

	static {
		DEFAULT_PROPERTIES.put("sleeping", false);
	}

	/**
	 * The frequency.
	 */
    private Frequency frequency;

	/**
	 * The start of this alarm.
	 */
	private ZonedDateTime start;

	/**
	 * The properties of this alarm.
	 */
    private Properties properties = new Properties(DEFAULT_PROPERTIES);

	/**
	 * Construct a new {@code Alarm} {@code Object}.
	 * @param frequency The frequency.
	 * @param start The start date.
	 */
	public Alarm(Frequency frequency, ZonedDateTime start) {
        this.frequency = frequency;
        this.start = start;
    }

	/**
	 * Get a property from this alarm.
	 * @param key The key of the property to get.
	 * @return The value of the property.
	 */
	public Object getProperty(String key) {
    	return properties.get(key);
	}

	/**
	 * Get the frequency.
	 * @return {@code frequency}
	 */
    public Frequency getFrequency() {
        return frequency;
    }

	/**
	 * Set a new frequency for this alarm.
	 * @param frequency The new frequency to set.
	 */
	public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

	/**
	 * Get the start date.
	 * @return {@code start}
	 */
    public ZonedDateTime getStart() {
        return start;
    }

	/**
	 * Set a new start date for this frequency.
	 * @param start The new start date to set.
	 */
	public void setStart(ZonedDateTime start) {
        this.start = start;
    }

}
