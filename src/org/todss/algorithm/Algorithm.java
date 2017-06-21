package org.todss.algorithm;

import org.todss.model.IntakeMoment;

import java.util.List;

/**
 * A class representing our base class for an algorithm implementation.
 * @author Displee
 */
public interface Algorithm {

	/**
	 * Run this algorithm.
	 * @param context The context.
	 * @return A list of intake moments.
	 */
	List<IntakeMoment> run(AlgorithmContext context);

}
