/*
 * Encog(tm) Core v2.6 Unit Test - Java Version
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
package org.encog.util;

/**
 * Utility classes for activation functions. Used to convert a single value
 * to/from an array. This is necessary because the activation functions are
 * designed to operate on arrays, rather than single values.
 * 
 * @author jheaton
 * 
 */
public final class ActivationUtil {

	/**
	 * Get a single value from an array. Return the first element in the array.
	 * 
	 * @param d
	 *            The array.
	 * @return The first element in the array.
	 */
	public static double fromArray(final double[] d) {
		return d[0];
	}

	/**
	 * Take a single value and create an array that holds it.
	 * 
	 * @param d
	 *            The single value.
	 * @return The array.
	 */
	public static double[] toArray(final double d) {
		final double[] result = new double[1];
		result[0] = d;
		return result;
	}

	/**
	 * Private constructor.
	 */
	private ActivationUtil() {
	}
}
