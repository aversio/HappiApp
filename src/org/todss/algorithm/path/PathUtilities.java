package org.todss.algorithm.path;

import org.todss.algorithm.SmartAlgorithm;
import org.todss.model.Frequency;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.todss.Constants.MAX_INTAKE_MOMENTS;

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
			paths.add(new Path(possibility));
		}
		return paths;
	}

	/**
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
	private static int[][] findPossiblePaths(int n, int target, int min, int max, int... steps) {
		int[] clonedInput = steps.clone();
		int[][] result = new int[(int) Math.pow(steps.length, n)][];
		int carry;
		int[] indices = new int[n];
		int resultIndex = 0;
		do {
			int total = 0;
			for(int i : indices) {
				final int step = clonedInput[i];
				if (step >= min && step <= max) {
					total += step;
				}
			}
			if (total == target) {
				int[] possibility = new int[indices.length];
				for (int i = 0; i < indices.length; i++) {
					possibility[i] = clonedInput[indices[i]];
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
		final ArrayList<int[]> list = new ArrayList<>();
		for(int[] set : result) {
			if (set != null) {
				list.add(set);
			}
		}
		return list.toArray(new int[list.size()][]);
	}

	/**
	 * Set the costs for a list of paths based on the distance of two dates.
	 * @param paths The paths.
	 * @param start The start date.
	 * @param arrival The arrival date.
	 * @param frequency The frequency.
	 */
	public static void setCosts(List<Path> paths, ZonedDateTime start, ZonedDateTime arrival, int difference, Frequency frequency) {
		for(Path path : paths) {
			//we create a copy here of start
			ZonedDateTime temp = start.minusHours(0);
			int cost = 0;
			//we create a copy here of temp
			final ZonedDateTime original = temp.minusHours(0).withZoneSameInstant(arrival.getZone());
			for(int i = 0; i < path.getSteps().length; i++) {
				final int step = path.getSteps()[i];
				temp = SmartAlgorithm.getNextIntakeDate(temp, frequency, difference < 0 ? -1 : 1);
				if (step < 0) {
					temp = temp.plusHours(step);
				} else {
					temp = temp.minusHours(step);
				}
				if (true) {
					System.out.println("Checking[original=" + original + ", temp=" + temp + ", step=" + step + ", duration=" + Duration.between(original, temp) + "]");
				}
				cost += Duration.between(original, temp).toMinutes();
			}
			path.setCost(cost);
		}
	}

	public static List<Path> findPathsAfterArrival(int min, int difference, ZonedDateTime start, ZonedDateTime arrival, Frequency frequency) {
		final List<Path> paths = PathUtilities.findPossiblePaths(min, frequency.getMargin(), difference);
		PathUtilities.setCosts(paths, start, arrival, difference, frequency);
		final List<Path> result = new ArrayList<>();
		for(Path path : paths) {
			ZonedDateTime next = SmartAlgorithm.getNextIntakeDate(start, frequency).withZoneSameLocal(arrival.getZone());
			final int step = path.getSteps()[0];
			if (step < 0) {
				next = next.plusHours(step);
			} else {
				next = next.minusHours(step);
			}
			if (next.getHour() > arrival.getHour()) {
				result.add(path);
			}
		}
		if (result.size() == 0 && min == MAX_INTAKE_MOMENTS) {
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
