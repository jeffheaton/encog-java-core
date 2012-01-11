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
 * A simple class that prevents numbers from getting either too big or too
 * small.
 */
public final class BoundNumbers {

	/**
	 * Too small of a number.
	 */
	public static final double TOO_SMALL = -1.0E20;

	/**
	 * Too big of a number.
	 */
	public static final double TOO_BIG = 1.0E20;

	/**
	 * Bound the number so that it does not become too big or too small.
	 * 
	 * @param d
	 *            The number to check.
	 * @return The new number. Only changed if it was too big or too small.
	 */
	public static double bound(final double d) {
		if (d < BoundNumbers.TOO_SMALL) {
			return BoundNumbers.TOO_SMALL;
		} else if (d > BoundNumbers.TOO_BIG) {
			return BoundNumbers.TOO_BIG;
		} else {
			return d;
		}
	}

	/**
	 * Private constructor.
	 */
	private BoundNumbers() {

	}
}
