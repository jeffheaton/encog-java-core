package org.encog.engine.network.train;

public class RPROPConst {
	/**
	 * The default zero tolerance.
	 */
	public static final double DEFAULT_ZERO_TOLERANCE = 0.00000000000000001;

	/**
	 * The POSITIVE ETA value. This is specified by the resilient propagation
	 * algorithm. This is the percentage by which the deltas are increased by if
	 * the partial derivative is greater than zero.
	 */
	public static final double POSITIVE_ETA = 1.2;

	/**
	 * The NEGATIVE ETA value. This is specified by the resilient propagation
	 * algorithm. This is the percentage by which the deltas are increased by if
	 * the partial derivative is less than zero.
	 */
	public static final double NEGATIVE_ETA = 0.5;

	/**
	 * The minimum delta value for a weight matrix value.
	 */
	public static final double DELTA_MIN = 1e-6;

	/**
	 * The starting update for a delta.
	 */
	public static final double DEFAULT_INITIAL_UPDATE = 0.1;

	/**
	 * The maximum amount a delta can reach.
	 */
	public static final double DEFAULT_MAX_STEP = 50;

}
