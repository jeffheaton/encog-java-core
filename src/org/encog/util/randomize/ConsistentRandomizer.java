package org.encog.util.randomize;

import org.encog.util.math.LinearCongruentialGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsistentRandomizer extends BasicRandomizer {

	private LinearCongruentialGenerator rand = new LinearCongruentialGenerator(1000);
	
	/**
	 * Generate a random number in the specified range.
	 * 
	 * @param min
	 *            The minimum value.
	 * @param max
	 *            The maximum value.
	 * @return A random number.
	 */
	public static double randomize(final double min, final double max) {
		final double range = max - min;
		return (range * Math.random()) + min;
	}

	/**
	 * The minimum value for the random range.
	 */
	private final double min;

	/**
	 * The maximum value for the random range.
	 */
	private final double max;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct a range randomizer.
	 * 
	 * @param min
	 *            The minimum random value.
	 * @param max
	 *            The maximum random value.
	 */
	public ConsistentRandomizer(final double min, final double max) {
		this.max = max;
		this.min = min;
	}

	/**
	 * Generate a random number based on the range specified in the constructor.
	 * 
	 * @param d
	 *            The range randomizer ignores this value.
	 * @return The random number.
	 */
	public double randomize(final double d) {
		return rand.range(this.min, this.max);
	}

}
