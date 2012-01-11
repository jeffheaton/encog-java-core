/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.mathutil;

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
	 * First part of default mod.
	 */
	public static final long DEFAULT_MOD1 = 2L;
	
	/**
	 * Second part of default mod.
	 */
	public static final long DEFAULT_MOD2 = 32L;
	
	/**
	 * Default mult.
	 */
	public static final long DEFAULT_MULT = 1103515245L;
	
	/**
	 * Default inc.
	 */
	public static final long DEFAULT_INC = 12345L;
	
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
	 * Construct the default LCG. You need only specify a seed.
	 * 
	 * @param theSeed
	 *            The seed to use.
	 */
	public LinearCongruentialGenerator(final long theSeed) {
		this((long) Math.pow(DEFAULT_MOD1, DEFAULT_MOD2), 
				DEFAULT_MULT, DEFAULT_INC, theSeed);
	}

	/**
	 * Create a LCG with the specified modulus, multiplier and increment. Unless
	 * you REALLY KNOW WHAT YOU ARE DOING, just use the constructor that just
	 * takes a seed. It will set these values to the same as set by the GCC C
	 * compiler. Setting these values wrong can create fairly useless random
	 * numbers.
	 * 
	 * @param theModulus
	 *            The modulus for the LCG algorithm.
	 * @param theMultiplier
	 *            The multiplier for the LCG algorithm.
	 * @param theIncrement
	 *            The increment for the LCG algorithm.
	 * @param theSeed
	 *            The seed for the LCG algorithm. Using the same seed will give
	 *            the same random number sequence each time, whether in Java or
	 *            DotNet.
	 */
	public LinearCongruentialGenerator(final long theModulus,
			final long theMultiplier, final long theIncrement, 
			final long theSeed) {
		super();
		this.modulus = theModulus;
		this.multiplier = theMultiplier;
		this.increment = theIncrement;
		this.seed = theSeed;
	}

	/**
	 * @return The LCG increment.
	 */
	public final long getIncrement() {
		return this.increment;
	}

	/**
	 * @return The LCG modulus.
	 */
	public final long getModulus() {
		return this.modulus;
	}

	/**
	 * @return The LCG multiplier.
	 */
	public final long getMultiplier() {
		return this.multiplier;
	}

	/**
	 * @return The current seed. Set to a constant to start, thereafter the
	 *         previously generated random number.
	 */
	public final long getSeed() {
		return this.seed;
	}

	/**
	 * @return The next random number as a double between 0 and 1.
	 */
	public final double nextDouble() {
		double result = (double) nextLong() / LinearCongruentialGenerator.MAX_RAND; 
		return result;
	}

	/**
	 * @return The next random number as a long between 0 and MAX_RAND.
	 */
	public final long nextLong() {
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
	public final double range(final double min, final double max) {
        double range = max - min;
        return (range*nextDouble()) + min;
	}

	/**
	 * Set the seed value. Setting a seed to a specific value will always result
	 * in the same sequence of numbers, whether on Java or DotNet.
	 * 
	 * @param theSeed
	 *            The seed value.
	 */
	public final void setSeed(final long theSeed) {
		this.seed = theSeed;
	}

}
