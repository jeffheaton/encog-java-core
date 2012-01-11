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

/**
 * Generally, you will not want to use this randomizer as a pure neural network
 * randomizer. More on this later in the description.
 * 
 * Generate random numbers that fall within a Gaussian curve. The mean
 * represents the center of the curve, and the standard deviation helps
 * determine the length of the curve on each side.
 * 
 * This randomizer is used mainly for special cases where I want to generate
 * random numbers in a Gaussian range. For a pure neural network initializer, it
 * leaves much to be desired. However, it can make for a decent randomizer.
 * Usually, the Nguyen Widrow randomizer performs better.
 * 
 * Uses the "Box Muller" method.
 * http://en.wikipedia.org/wiki/Box%E2%80%93Muller_transform
 * 
 * Ported from C++ version provided by Everett F. Carter Jr., 1994
 */
public class GaussianRandomizer extends BasicRandomizer {

	/**
	 * The y2 value.
	 */
	private double y2;

	/**
	 * Should we use the last value.
	 */
	private boolean useLast = false;

	/**
	 * The mean.
	 */
	private final double mean;

	/**
	 * The standard deviation.
	 */
	private final double standardDeviation;

	/**
	 * Construct a Gaussian randomizer. The mean, the standard deviation.
	 * 
	 * @param mean
	 *            The mean.
	 * @param standardDeviation
	 *            The standard deviation.
	 */
	public GaussianRandomizer(final double mean, 
				final double standardDeviation) {
		this.mean = mean;
		this.standardDeviation = standardDeviation;
	}

	/**
	 * Compute a Gaussian random number.
	 * 
	 * @param m
	 *            The mean.
	 * @param s
	 *            The standard deviation.
	 * @return The random number.
	 */
	public double boxMuller(final double m, final double s) {
		double x1, x2, w, y1;

		// use value from previous call
		if (this.useLast) {
			y1 = this.y2;
			this.useLast = false;
		} else {
			do {
				x1 = 2.0 * nextDouble() - 1.0;
				x2 = 2.0 * nextDouble() - 1.0;
				w = x1 * x1 + x2 * x2;
			} while (w >= 1.0);

			w = Math.sqrt((-2.0 * Math.log(w)) / w);
			y1 = x1 * w;
			this.y2 = x2 * w;
			this.useLast = true;
		}

		return (m + y1 * s);
	}

	/**
	 * Generate a random number.
	 * 
	 * @param d
	 *            The input value, not used.
	 * @return The random number.
	 */
	public double randomize(final double d) {
		return boxMuller(this.mean, this.standardDeviation);
	}

}
