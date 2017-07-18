package org.todss.algorithm.path;

import java.util.Arrays;

/**
 * A class representing a single path that's being used by the SmartAlgorithm class.
 * @author Displee
 */
public class Path {

	/**
	 * The steps of this path.
	 */
	private final int[] steps;

	/**
	 * The cost in minutes.
	 */
	private int cost;

	/**
	 * Construct a new {@code Path} {@code Object}.
	 * @param steps The steps.
	 */
	public Path(int... steps) {
		this.steps = steps;
	}

	/**
	 * Get the last step.
	 * @return The last step of this path.
	 */
	public int getLastStep() {
		return steps[steps.length - 1];
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Path)) {
			return false;
		}
		Path other = (Path) obj;
		return Arrays.equals(steps, other.steps);
	}

	/**
	 * Get the steps of this path.
	 * @return {@code path}
	 */
	public int[] getSteps() {
		return steps;
	}

	/**
	 * Set the cost for this path.
	 * @param cost The new cost to set.
	 */
	public void setCost(int cost) {
		this.cost = cost;
	}

	/**
	 * Get the cost.
	 * @return {@code cost}
	 */
	public int getCost() {
		return cost;
	}

	@Override
	public String toString() {
		return "Path[steps=" + Arrays.toString(steps) + ", cost=" + cost + "]";
	}

}
