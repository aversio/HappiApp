package org.todss.algorithm.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * A class representing serving as result of a demarcation process.
 * @author Displee
 */
public class DemarcateResult {

	/**
	 * The path.
	 */
	private final org.todss.algorithm.path.Path path;

	/**
	 * If we have to add the current intake inside the algorithm main loop.
	 */
	private final boolean addCurrent;

	/**
	 * A map containing new intakes with their indices.
	 */
	private final Map<Integer, Intake> map;

	/**
	 * Create a new {@code DemarcateResult} {@code Object}.
	 * @param path The path.
	 * @param addCurrent If we have to add the current intake.
	 * @param map The map with our new intakes.
	 */
	public DemarcateResult(org.todss.algorithm.path.Path path, boolean addCurrent, Map<Integer, Intake> map) {
		this.path = path;
		this.addCurrent = addCurrent;
		this.map = map;
	}

	/**
	 * Check if any intake is planned between the travel departure and arrival.
	 * @param travel The travel.
	 * @return If a intake is planned between the travel.
	 */
	public boolean betweenTravel(Travel travel) {
		for(Map.Entry<Integer, Intake> entry : map.entrySet()) {
			final ZonedDateTime date = entry.getValue().getDate();
			if (date.isAfter(travel.getDeparture()) && date.isBefore(travel.getArrival())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the first intake.
	 * @return The first intake.
	 */
	public Intake getFirst() {
		return map.entrySet().iterator().next().getValue();
	}

	/**
	 * Get the last intake.
	 * @return The last intake.
	 */
	public Intake getLast() {
		Intake[] array = map.values().toArray(new Intake[0]);
		return array[array.length - 1];
	}

	/**
	 * Populate the {@code list} with our new intakes.
	 * @param list The list to populate.
	 */
	public void populate(List<Intake> list) {
		for(Map.Entry<Integer, Intake> entry : map.entrySet()) {
			list.set(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Check if this result is a valid result.
	 * @return If it has no invalid intakes.
	 */
	public boolean isValid() {
		return getInvalidIntakes() == 0;
	}

	/**
	 * Get the amount of invalid intakes.
	 * @return The amount of invalid intakes.
	 */
	public int getInvalidIntakes() {
		int count = 0;
		for(Map.Entry<Integer, Intake> entry : map.entrySet()) {
			final ZonedDateTime date = entry.getValue().getDate();
			if (date.getHour() > 22 || date.getHour() < 8) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Compares the amount of invalid intakes with another {@code other}.
	 * @param hour The hour of a default intake moment.
	 * @param other The other intake to compare with.
	 * @return Returns true if the {@code other} intake contains less invalid intakes or contains less invalid hours.
	 */
	public boolean compareInvalidIntakes(int hour, DemarcateResult other) {
		final int currentInvalid = getInvalidIntakes();
		final int otherInvalid = other.getInvalidIntakes();
		if (otherInvalid < currentInvalid) {
			return true;
		} else if (otherInvalid > currentInvalid) {
			return false;
		}
		for(int key : map.keySet()) {
			final Intake intake = map.get(key);
			final Intake otherIntake = other.map.get(key);
			//System.out.println(intake + " - " + otherIntake);
			if (otherIntake == null) {
				return false;
			}
			if (hour - intake.getDate().getHour() < hour - otherIntake.getDate().getHour()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the path.
	 * @return {@code path}
	 */
	public org.todss.algorithm.path.Path getPath() {
		return path;
	}

	/**
	 * If we have to add the current date inside the algorithm loop.
	 * @return {@code addCurrent}
	 */
	public boolean addCurrent() {
		return addCurrent;
	}

}
