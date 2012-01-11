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
 * A randomizer that distorts what is already present in the neural network.
 * 
 * @author jheaton
 * 
 */
public class Distort extends BasicRandomizer {

	/**
	 * The factor to use to distort the numbers.
	 */
	private final double factor;

	/**
	 * Construct a distort randomizer for the specified factor.
	 * 
	 * @param factor
	 *            The randomizer factor.
	 */
	public Distort(final double factor) {
		this.factor = factor;
	}

	/**
	 * Distort the random number by the factor that was specified in the
	 * constructor.
	 * 
	 * @param d
	 *            The number to distort.
	 * @return The result.
	 */
	public double randomize(final double d) {
		return d + (this.factor - (nextDouble() * this.factor * 2));
	}

}
