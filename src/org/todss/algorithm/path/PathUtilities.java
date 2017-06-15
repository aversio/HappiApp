package org.todss.algorithm.path;

import org.todss.algorithm.SmartAlgorithm;
import org.todss.model.Frequency;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class used to utilize the path system of this application.
 * @author Displee
 */
public class PathUtilities {

	/**
	 * The default steps a {@link Path} can contain.
	 */
	private static final int[] PATH_STEPS = getPathSteps();

	/**
	 * Calculate the default steps a {@link Path} can contain.
	 * @return The default steps.
	 */
	private static int[] getPathSteps() {
		int highest = 0;
		for(Frequency f : Frequency.values()) {
			if (f.getMargin() > highest) {
				highest = f.getMargin();
			}
		}
		final int[] array = new int[(highest * 2) + 1];
		for(int i = 0; i < array.length; i++) {
			array[i] = -highest + i;
		}
		return array;
	}

	/**
	 * Find all possible paths that sum the total {@code target} with a specific path length of {@code n}.
	 * @param n The maximum length of the possible paths.
	 * @param margin The margin used as minimum and maximum step.
	 * @param target The target amount.
	 * @return A list of possible paths.
	 */
	public static List<Path> findPossiblePaths(int n, int margin, int target) {
		final List<Path> paths = new ArrayList<>();
		final int[][] possibilities = findPossiblePaths(n, target, -margin, margin, PATH_STEPS);
		for(int[] possibility : possibilities) {
			if (possibility != null) {
				paths.add(new Path(possibility));
			}
		}
		return paths;
	}

	/**
	 * Find all possible paths with {@code steps} that sum the {@code target} where the path has a maximum length of {@code n}.
	 * @param n The maximum path length.
	 * @param target The target amount.
	 * @param steps The steps
	 * @return A 2D array of paths.
	 */
	private static int[][] findPossiblePaths(int n, int target, int min, int max, int... steps) {
		int[] clonedInput = steps.clone();
		int[][] result = new int[(int) Math.pow(steps.length, n)][];
		int carry;
		int[] indices = new int[n];
		int resultIndex = 0;
		do {
			int total = 0;
			for(int i : indices) {
				total += clonedInput[i];
			}
			if (total == target) {
				int[] possibility = new int[indices.length];
				for (int i = 0; i < indices.length; i++) {
					int step = clonedInput[indices[i]];
					if (step >= min && step <= max) {
						possibility[i] = step;
					}
				}
				result[resultIndex++] = possibility;
			}
			carry = 1;
			for (int i = indices.length - 1; i >= 0; i--) {
				if (carry == 0) {
					break;
				}
				indices[i] += carry;
				carry = 0;
				if (indices[i] == clonedInput.length) {
					carry = 1;
					indices[i] = 0;
				}
			}
		}
		while (carry != 1);
		return result;
	}

	/**
	 * Set the costs for a list of paths based on the distance of two dates.
	 * @param paths The paths.
	 * @param start The start date.
	 * @param arrival The arrival date.
	 * @param frequency The frequency.
	 */
	public static void setCosts(List<Path> paths, ZonedDateTime start, ZonedDateTime arrival, Frequency frequency) {
		for(Path path : paths) {
			//we create a copy here of start
			ZonedDateTime temp = start.minusHours(0);
			int cost = 0;
			//we create a copy here of temp
			final ZonedDateTime original = temp.minusHours(0);
			for(int i = 0; i < path.getSteps().length; i++) {
				final int step = path.getSteps()[i];
				temp = SmartAlgorithm.getNextIntakeDate(temp, frequency).withZoneSameLocal(arrival.getZone());
				if (step < 0) {
					temp = temp.plusHours(step);
				} else {
					temp = temp.minusHours(step);
				}
				cost += Duration.between(original, temp).toMinutes();
			}
			path.setCost(cost);
		}
	}

	public static List<Path> findPathsAfterArrival(int min, int difference, ZonedDateTime start, ZonedDateTime arrival, Frequency frequency) {
		final List<Path> paths = PathUtilities.findPossiblePaths(min, frequency.getMargin(), difference);
		final List<Path> result = new ArrayList<>();
		for(Path path : paths) {
			ZonedDateTime next = SmartAlgorithm.getNextIntakeDate(start, frequency).withZoneSameLocal(arrival.getZone());
			final int step = path.getSteps()[0];
			if (step < 0) {
				next = next.plusHours(step);
				if (next.getHour() < arrival.getHour()) {
					result.add(path);
				}
			} else {
				next = next.minusHours(step);
				if (next.getHour() > arrival.getHour()) {
					result.add(path);
				}
			}
		}
		return result;
	}

	/**
	 * Find the shortest path in a list of paths.
	 * @param paths The paths.
	 * @return The short path.
	 */
	public static Path getShortestPath(List<Path> paths) {
		Path shortest = null;
		for(Path path : paths) {
			if (shortest == null || path.getCost() < shortest.getCost()) {
				shortest = path;
			}
		}
		return shortest;
	}

}
