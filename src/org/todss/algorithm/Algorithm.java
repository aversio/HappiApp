package org.todss.algorithm;

import org.todss.algorithm.model.Intake;

import java.util.List;

/**
 * A class representing our base class for an algorithm implementation.
 * @author Displee
 */
public interface Algorithm {
	String name();

	/**
	 * Run this algorithm.
	 * @param context The context.
	 * @return A list of intake moments.
	 */
	List<Intake> run(AlgorithmContext context);
}
