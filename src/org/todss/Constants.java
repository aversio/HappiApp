package org.todss;

/**
 * A class holding the constants for this application.
 * @author Displee
 */
public class Constants {

	/**
	 * The maximum intake moment we can move/shift/demarcate.
	 */
	public static int MAX_INTAKE_MOMENTS = 3;

	/**
	 * Whether we're allowed to plan an intake moment while traveling.
	 * This will only be used when the algorithm couldn't find any other solution.
	 */
	public static boolean ENABLE_INTAKE_WHILE_TRAVELING = true;

}
