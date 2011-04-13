package org.encog.ml;

/**
 * Specifies the type of training that an object provides.
 */
public enum TrainingImplementationType {
	/**
	 * Iterative - Each iteration attempts to improve the machine 
	 * learning method.
	 */
	Iterative,
	
	/**
	 * Background - Training continues in the background until it is
	 * either finished or is stopped.
	 */
	Background,
	
	/**
	 * Single Pass - Only one iteration is necessary.
	 */
	OnePass
}
