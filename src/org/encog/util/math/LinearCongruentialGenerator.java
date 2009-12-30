/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.util.math;

/**
 * A predictable random number generator. This is useful for unit tests and
 * benchmarks where we want random numbers, but we want them to be the same each
 * time. This class exists on both Java and C# so it can even provide consistent
 * random numbers over the two platforms.
 * 
 * Random numbers are created using a LCG.
 * 
 * http://en.wikipedia.org/wiki/Linear_congruential_generator
 */
public class LinearCongruentialGenerator {

	/**
	 * The modulus.
	 */
	private final long modulus;

	/**
	 * The multiplier.
	 */
	private final long multiplier;

	/**
	 * The amount to increment.
	 */
	private final long increment;

	/**
	 * The current seed, set to an initial value and always holds the value of
	 * the last random number generated.
	 */
	private long seed;

	/**
	 * The maximum rand number that the standard GCC based LCG will generate.
	 */
	public static final long MAX_RAND = 4294967295L;

	/**
	 * Construct the default LCG.  You need only specify a seed.
	 * @param seed
	 */
	public LinearCongruentialGenerator(final long seed) {
		this((long) Math.pow(2L, 32L), 1103515245L, 12345L, seed);
	}

	/**
	 * Create a LCG with the specified modulus, multiplier and increment. Unless
	 * you REALLY KNOW WHAT YOU ARE DOING, just use the constructor that just
	 * takes a seed. It will set these values to the same as set by the GCC C
	 * compiler. Setting these values wrong can create fairly useless random
	 * numbers.
	 * 
	 * @param modulus
	 *            The modulus for the LCG algorithm.
	 * @param multiplier
	 *            The multiplier for the LCG algorithm.
	 * @param increment
	 *            The increment for the LCG algorithm.
	 * @param seed
	 *            The seed for the LCG algorithm. Using the same seed will give
	 *            the same random number sequence each time, whether in Java or
	 *            DotNet.
	 */
	public LinearCongruentialGenerator(final long modulus,
			final long multiplier, final long increment, final long seed) {
		super();
		this.modulus = modulus;
		this.multiplier = multiplier;
		this.increment = increment;
		this.seed = seed;
	}

	/**
	 * @return The LCG increment.
	 */
	public long getIncrement() {
		return this.increment;
	}

	/**
	 * @return The LCG modulus.
	 */
	public long getModulus() {
		return this.modulus;
	}

	/**
	 * @return The LCG multiplier.
	 */
	public long getMultiplier() {
		return this.multiplier;
	}

	/**
	 * @return The current seed. Set to a constant to start, thereafter the
	 *         previously generated random number.
	 */
	public long getSeed() {
		return this.seed;
	}

	/**
	 * @return The next random number as a double between 0 and 1.
	 */
	public double nextDouble() {
		return (double) nextLong() / LinearCongruentialGenerator.MAX_RAND;
	}

	/**
	 * @return The next random number as a long between 0 and MAX_RAND.
	 */
	public long nextLong() {
		this.seed = (this.multiplier * this.seed + this.increment)
				% this.modulus;
		return this.seed;
	}

	/**
	 * Generate a random number in the specified range.
	 * 
	 * @param min
	 *            The minimum random number.
	 * @param max
	 *            The maximum random number.
	 * @return The generated random number.
	 */
	public double range(final double min, final double max) {
		final double range = max - min;
		return (range * nextDouble()) + min;
	}

	/**
	 * Set the seed value. Setting a seed to a specific value will always result
	 * in the same sequence of numbers, whether on Java or DotNet.
	 * 
	 * @param seed The seed value.
	 */
	public void setSeed(final long seed) {
		this.seed = seed;
	}

}
