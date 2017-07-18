package org.todss.algorithm.path;

import org.todss.algorithm.impl.SmartAlgorithm;
import org.todss.algorithm.model.DemarcateResult;
import org.todss.algorithm.model.Frequency;
import org.todss.algorithm.model.Intake;
import org.todss.algorithm.model.Travel;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;

import static org.todss.algorithm.Constants.ENABLE_INTAKE_WHILE_TRAVELING;

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
	 * Calculate the steps a {@link Path} can contain.
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
	 * @param steps The steps.
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
	 * Find paths that are after or before the target date.
	 * @param steps The minimum amount of steps.
	 * @param difference The time difference.
	 * @param start The start date.
	 * @param travel The travel.
	 * @param frequency The frequency.
	 * @param after If we demarcate after the travel.
	 * @return A list of paths.
	 */
	public static List<Path> findPathsForTargetDate(int steps, int difference, ZonedDateTime start, Travel travel, Frequency frequency, boolean after) {
		//final ZonedDateTime target = after ? travel.getArrival() : travel.getDeparture();
		final List<Path> paths = PathUtilities.findPossiblePaths(steps, frequency.getMargin(), difference);
		setCosts(steps, paths, travel, start, frequency, after);
		final List<Path> result = new ArrayList<>();
		for(Path path : paths) {
			final DemarcateResult demarcateResult = process(path, travel, start, frequency, 0, after);
			//Skip paths that are between the travel.
			if (demarcateResult.betweenTravel(travel)) {
				continue;
			}
			result.add(path);
			/*ZonedDateTime next = (after ? demarcateResult.getFirst() : demarcateResult.getLast()).getDate();
			if (after && next.getZone() != travel.getArrival().getZone()) {
				next = next.withZoneSameInstant(travel.getArrival().getZone());
			}
			System.out.println("Faka deze=" + next + ", path=" + path + ", target_hour=" + target.getHour() + ", after=" + after);
			if (after) {
				final int targetHour = target.getHour() + (target.getMinute() == 0 ? 0 : 1);
				if (next.getHour() >= targetHour) {
					result.add(path);
				}
			} else {
				if (next.isBefore(target)) {
					result.add(path);
				}
			}*/
		}
		if (ENABLE_INTAKE_WHILE_TRAVELING && result.size() == 0) {
			result.add(getShortestPath(paths));
		}
		return result;
	}

	/**
	 * Run the demarcate process for the argued travel.
	 * TODO Bug: when the travel takes less than the time difference of the arrival zone, it will fuckup the intake moment zones.
	 * @param path The path we take.
	 * @param travel The travel.
	 * @param next The start date.
	 * @param frequency The frequency.
	 * @param currentIndex The current loop index.
	 * @param after If we demarcate after the travel.
	 * @return A demarcate result containing our new intake moments.
	 */
	public static DemarcateResult process(Path path, Travel travel, ZonedDateTime next, Frequency frequency, int currentIndex, boolean after) {
		boolean addCurrent = true;
		final Map<Integer, Intake> map = new LinkedHashMap<>();
		if (after) {
			next = next.withZoneSameInstant(travel.getArrival().getZone());
		} else {
			next = SmartAlgorithm.getNextIntakeDate(next, frequency, -path.getSteps().length + 1);
		}
		final int first = after ? 0 : (path.getSteps().length - 1);
		//final int last = after ? (path.getSteps().length - 1) : 0;
		int i = first;
		//int cost = 0;
		while(after ? (i < path.getSteps().length) : (i >= 0)) {
			final int step = path.getSteps()[i];
			if (i != first) {
				next = SmartAlgorithm.getNextIntakeDate(next, frequency);
			}
			if (after) {
				next = next.withZoneSameLocal(travel.getArrival().getZone());
			}
			if (after) {
				next = next.plusHours(step);
			} else {
				next = next.minusHours(step);
			}
			ZonedDateTime toAdd = next;
			if (after && i == first) {
				addCurrent = false;
			}
			if (next.isBefore(travel.getArrival())) {
				toAdd = next.withZoneSameInstant(travel.getDeparture().getZone());
			} else if (next.isAfter(travel.getArrival())) {
				toAdd = next.withZoneSameInstant(travel.getArrival().getZone());
			}
			final int newIndex = after ? (currentIndex + i) : (currentIndex - i + 1);
			map.put(newIndex, new Intake(toAdd));
			if (after) {
				i++;
			} else {
				i--;
			}
		}
		final DemarcateResult result = new DemarcateResult(path, addCurrent, map);
		//determine the costs based on how fast we can get to our desired last intake
		//TODO Fix costs, currently uses a not logical concept, but it works. See #setCosts method.
		/*final Intake lastIntake = result.getLast();
		int position = 1;
		for(Map.Entry<Integer, Intake> entry : map.entrySet()) {
			cost += Duration.between(original, entry.getValue().getDate()).toMinutes();
		}*/
		/*for (Map.Entry<Integer, Intake> entry : map.entrySet()) {
			if (after) {
				cost += Duration.between(idk, entry.getValue().getDate()).toMinutes();
			} else {
				cost += Duration.between(entry.getValue().getDate(), lastIntake.getDate()).toMinutes();
			}
		}*/
		//path.setCost(cost);
		return result;
	}

	/**
	 * Set the costs of the argued paths.
	 * //TODO Fix this. It's currently not logical, but it works.
	 * @param steps The amount of steps to take.
	 * @param paths The paths.
	 * @param travel The travel.
	 * @param start The start date.
	 * @param frequency The frequency.
	 * @param after If we demarcate after the travel.
	 */
	private static void setCosts(int steps, List<Path> paths, Travel travel, ZonedDateTime start, Frequency frequency, boolean after) {
		for(Path path : paths) {
			//final DemarcateResult result = process(path, travel, start, frequency, 0, after);
			//we create a copy here of start
			ZonedDateTime next = start.minusHours(0);
			int cost = 0;
			//we create a copy here of temp
			final ZonedDateTime original;
			if (after) {
				original = next.withZoneSameInstant(travel.getArrival().getZone());
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
