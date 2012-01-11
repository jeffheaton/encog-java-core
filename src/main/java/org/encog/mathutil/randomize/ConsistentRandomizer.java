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
package org.encog.mathutil.randomize;

import org.encog.mathutil.LinearCongruentialGenerator;
import org.encog.neural.networks.BasicNetwork;

/**
 * A randomizer that takes a seed and will always produce consistent results.
 */
public class ConsistentRandomizer extends BasicRandomizer {

	/**
	 * The generator.
	 */
	private final LinearCongruentialGenerator rand;

	/**
	 * The minimum value for the random range.
	 */
	private final double min;

	/**
	 * The maximum value for the random range.
	 */
	private final double max;
	
	/**
	 * The seed.
	 */
	private final int seed;

	/**
	 * Construct a range randomizer.
	 * 
	 * @param min
	 *            The minimum random value.
	 * @param max
	 *            The maximum random value.
	 */
	public ConsistentRandomizer(final double min, final double max) {
		this(min, max, 1000);
	}

	/**
	 * Construct a range randomizer.
	 * 
	 * @param min
	 *            The minimum random value.
	 * @param max
	 *            The maximum random value.
	 * @param seed	The seed value.
	 */
	public ConsistentRandomizer(final double min, final double max,
			final int seed) {
		this.max = max;
		this.min = min;
		this.seed = seed;
		this.rand = new LinearCongruentialGenerator(seed);
	}


	/**
	 * Generate a random number based on the range specified in the constructor.
	 * 
	 * @param d
	 *            The range randomizer ignores this value.
	 * @return The random number.
	 */
	public double randomize(final double d) {
		return this.rand.range(this.min, this.max);
	}
	
	/**
	 * Randomize the network.
	 * @param network The network to randomize.
	 */
	public void randomize(final BasicNetwork network) {
		this.rand.setSeed(this.seed);
		super.randomize(network);
	}

}
