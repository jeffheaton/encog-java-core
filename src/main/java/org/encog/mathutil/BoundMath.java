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
 * Java will sometimes return Math.NaN or Math.Infinity when numbers get to
 * large or too small. This can have undesirable effects. This class provides
 * some basic math functions that may be in danger of returning such a value.
 * This class imposes a very large and small ceiling and floor to keep the
 * numbers within range.
 * 
 * @author jheaton
 * 
 */
public final class BoundMath {

	/**
	 * Calculate the cos.
	 * 
	 * @param a
	 *            The value passed to the function.
	 * @return The result of the function.
	 */
	public static double cos(final double a) {
		return BoundNumbers.bound(Math.cos(a));
	}

	/**
	 * Calculate the exp.
	 * 
	 * @param a
	 *            The value passed to the function.
	 * @return The result of the function.
	 */
	public static double exp(final double a) {
		return BoundNumbers.bound(Math.exp(a));
	}

	/**
	 * Calculate the log.
	 * 
	 * @param a
	 *            The value passed to the function.
	 * @return The result of the function.
	 */
	public static double log(final double a) {
		return BoundNumbers.bound(Math.log(a));
	}

	/**
	 * Calculate the power of a number.
	 * 
	 * @param a
	 *            The base.
	 * @param b
	 *            The exponent.
	 * @return The result of the function.
	 */
	public static double pow(final double a, final double b) {
		return BoundNumbers.bound(Math.pow(a, b));
	}

	/**
	 * Calculate the sin.
	 * 
	 * @param a
	 *            The value passed to the function.
	 * @return The result of the function.
	 */
	public static double sin(final double a) {
		return BoundNumbers.bound(Math.sin(a));
	}

	/**
	 * Calculate the square root.
	 * 
	 * @param a
	 *            The value passed to the function.
	 * @return The result of the function.
	 */
	public static double sqrt(final double a) {
		return Math.sqrt(a);
	}

	/**
	 * Private constructor.
	 */
	private BoundMath() {

	}

	/**
	 * Calculate TANH, within bounds.
	 * @param d The value to calculate for.
	 * @return The result.
	 */
	public static double tanh(final double d) {
		return BoundNumbers.bound(Math.tanh(d));
	}
}
