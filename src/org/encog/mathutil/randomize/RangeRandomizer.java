/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A randomizer that will create random weight and bias values that are
 * between a specified range.
 *
 * @author jheaton
 *
 */
public class RangeRandomizer extends BasicRandomizer {

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
	public RangeRandomizer(final double min, final double max) {
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
		return nextDouble(this.min, this.max);
	}

	public static int randomInt(int min, int max) {
		return (int)randomize(min,max+1);
	}

}
