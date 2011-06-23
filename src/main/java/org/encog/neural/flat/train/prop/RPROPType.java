package org.encog.neural.flat.train.prop;

/**
 * Allows the type of RPROP to be defined.  RPROPp is the classic RPROP.
 * 
 * For more information, visit:
 * 
 * http://www.heatonresearch.com/wiki/RPROP
 *
 */
public enum RPROPType {
	/**
	 * RPROP+ : The classic RPROP algorithm.  Uses weight back tracking.
	 */
	RPROPp,
	
	/**
	 * RPROP- : No weight back tracking.
	 */
	RPROPm,
	
	/**
	 * iRPROP+ : New weight back tracking method, some consider this to be
	 * the most advanced RPROP.
	 */
	iRPROPp,
	
	/**
	 * iRPROP- : New RPROP without weight back tracking. 
	 */
	iRPROPm
}
