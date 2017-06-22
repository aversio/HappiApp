package org.todss.algorithm.path;

import org.todss.algorithm.impl.SmartAlgorithm;
import org.todss.algorithm.model.Frequency;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.todss.algorithm.Constants.ENABLE_INTAKE_WHILE_TRAVELING;
import static org.todss.algorithm.Constants.MAX_INTAKE_MOMENTS;

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
	private static List<Path> findPossiblePaths(int n, int margin, int target) {
		return findPossiblePaths(n, target, -margin, margin, PATH_STEPS);
	}

	/**
	 * //TODO Fix documnntation
	 * Find all possible paths with {@code steps} that sum the {@code target} where the path must contain {@code n} steps.
	 * A step must have a minimum size of {@code min} and can have a maximum size of {@code max}.
	 * <pre>
	 * {@code
	 * int[][] solutions = findPossiblePaths(3, 4, -2, 2, new int[] { -4, -3, -2, -1, 0, 1, 2, 3, 4});
	 * output: [[-4, 2, 2], [-3, 2, 2], [0, 2, 2], [1, 1, 2], [1, 2, 1], [2, -4, 2], [2, -3, 2], [2, 0, 2], [2, 1, 1],
	 * 			[2, 2, -4], [2, 2, -3], [2, 2, 0], [2, 2, 3], [2, 2, 4], [2, 3, 2], [2, 4, 2], [3, 2, 2], [4, 2, 2]]
	 * }
	 * </pre>
	 * @param n The maximum path length.
	 * @param target The target amount.
	 * @param min The minimum step size.
	 * @param max The maximum step size.
	 * @param steps The steps
	 * @return A 2D array of paths.
	 */
	private static List<Path> findPossiblePaths(int n, int target, int min, int max, int... steps) {
		int[] clonedInput = steps.clone();
		final List<Path> result = new ArrayList<>();
		int carry;
		int[] indices = new int[n];
		do {
			int total = 0;
			for(int i : indices) {
				final int step = clonedInput[i];
				if (step >= min && step <= max) {
					total += step;
				}
			}
			if (total == target) {
				final int[] possibility = new int[indices.length];
				for (int i = 0; i < indices.length; i++) {
					final int step = clonedInput[indices[i]];
					if (step >= min && step <= max) {
						possibility[i] = step;
					}
				}
				final Path path = new Path(possibility);
				if (!result.contains(path)) {
					result.add(path);
				}
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
	private static void setCosts(int steps, List<Path> paths, ZonedDateTime start, ZonedDateTime arrival, Frequency frequency, boolean after) {
		for(Path path : paths) {
			//we create a copy here of start
			ZonedDateTime next = start.minusHours(0);
			int cost = 0;
			//we create a copy here of temp
			final ZonedDateTime original;
			if (after) {
				original = next.withZoneSameInstant(arrival.getZone());
			} else {
				original = SmartAlgorithm.getNextIntakeDate(next, frequency, -steps);
			}
			for(int i = 0; i < path.getSteps().length; i++) {
				final int step = path.getSteps()[i];
				if (i != 0 || !after) {
					next = SmartAlgorithm.getNextIntakeDate(next, frequency, after ? 1 : -1);
				}
				if (step < 0) {
					next = next.plusHours(step);
				} else {
					next = next.minusHours(step);
				}
				cost += Duration.between(original, next).toMinutes();
			}
			path.setCost(cost);
		}
	}

	/**
	 * Find paths that are after or before the target date.
	 * @param steps The minimum amount of steps.
	 * @param difference The time difference.
	 * @param start The start date.
	 * @param targetDate The target date.
	 * @param frequency The frequency.
	 * @param after If we demarcate afterwards.
	 * @return A list of paths.
	 */
	public static List<Path> findPathsForTargetDate(int steps, int difference, ZonedDateTime start, ZonedDateTime targetDate, Frequency frequency, boolean after) {
		final List<Path> paths = PathUtilities.findPossiblePaths(steps, frequency.getMargin(), difference);
		PathUtilities.setCosts(steps, paths, start, targetDate, frequency, after);
		final List<Path> result = new ArrayList<>();
		for(Path path : paths) {
			ZonedDateTime next = SmartAlgorithm.getNextIntakeDate(start, frequency, after).withZoneSameLocal(targetDate.getZone());
			final int step;
			if (after) {
				step = path.getSteps()[0];
			} else {
				step = path.getLastStep();
			}
			if (step < 0) {
				next = next.plusHours(step);
			} else {
				next = next.minusHours(step);
			}
			if (after) {
				if (next.getHour() > targetDate.getHour()) {
					result.add(path);
				}
			} else {
				if (next.isBefore(targetDate)) {
					result.add(path);
				}
			}
		}
		if (ENABLE_INTAKE_WHILE_TRAVELING && result.size() == 0) {
			result.add(getShortestPath(paths));
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
			if (shortest == null || Math.abs(path.getCost()) < Math.abs(shortest.getCost())) {
				shortest = path;
			}
		}
		return shortest;
	}

}
