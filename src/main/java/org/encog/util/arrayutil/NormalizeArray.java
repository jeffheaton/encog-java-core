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
package org.encog.util.arrayutil;


/**
 * Normalization is the process where data is adjusted to be inside a range.
 * This range is typically -1 to 1. For more information about normalization,
 * refer to the following page.
 * 
 * http://www.heatonresearch.com/content/really-simple-introduction-
 * normalization
 * 
 * This class is used to normalize an array. Sometimes you would like to
 * normalize an array, rather than an entire CSV file. If you would like to
 * normalize an entire CSV file, you should make use of the class NormalizeCSV.
 */
public class NormalizeArray {

	/**
	 * Contains stats about the array normalized.
	 */
	private NormalizedField stats;

	/**
	 * The high end of the range that the values are normalized into. Typically
	 * 1.
	 */
	private double normalizedHigh;

	/**
	 * The low end of the range that the values are normalized into. Typically
	 * 1.
	 */
	private double normalizedLow;

	/**
	 * Construct the object, default NormalizedHigh and NormalizedLow to 1 and
	 * -1.
	 */
	public NormalizeArray() {
		this.normalizedHigh = 1;
		this.normalizedLow = -1;
	}

	/**
	 * @return The high value to normalize to.
	 */
	public final double getNormalizedHigh() {
		return this.normalizedHigh;
	}

	/**
	 * @return The low value to normalize to.
	 */
	public final double getNormalizedLow() {
		return this.normalizedLow;
	}

	/**
	 * @return Contains stats about the array normalized.
	 */
	public final NormalizedField getStats() {
		return this.stats;
	}

	/**
	 * Normalize the array. Return the new normalized array.
	 * 
	 * @param inputArray
	 *            The input array.
	 * @return The normalized array.
	 */
	public final double[] process(final double[] inputArray) {
		this.stats = new NormalizedField();
		this.stats.setNormalizedHigh(this.normalizedHigh);
		this.stats.setNormalizedLow(this.normalizedLow);

		for (final double element : inputArray) {
			this.stats.analyze(element);
		}

		final double[] result = new double[inputArray.length];

		for (int i = 0; i < inputArray.length; i++) {
			result[i] = this.stats.normalize(inputArray[i]);
		}

		return result;
	}

	/**
	 * Set the high value to normalize to.
	 * @param theNormalizedHigh The high value to normalize to.
	 */
	public final void setNormalizedHigh(final double theNormalizedHigh) {
		this.normalizedHigh = theNormalizedHigh;
	}

	/**
	 * Set the low value to normalize to.
	 * @param theNormalizedLow The low value to normalize to.
	 */
	public final void setNormalizedLow(final double theNormalizedLow) {
		this.normalizedLow = theNormalizedLow;
	}

}
