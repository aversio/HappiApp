package org.todss.algorithm;

import org.todss.algorithm.model.Intake;

import java.util.List;

/**
 * An interface representing a skeleton for an algorithm implementation.
 * @author Displee
 */
public interface Algorithm {

	/**
	 * Get the name of this algorithm.
	 * @return The name of this algorithm.
	 */
	String name();

	/**
	 * Run this algorithm.
	 * @param context The context.
	 * @return A list of intake moments.
	 */
	List<Intake> run(AlgorithmContext context);

}
