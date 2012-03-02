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
package org.encog.ml.bayesian;

import java.io.Serializable;

import org.encog.Encog;
import org.encog.util.csv.CSVFormat;

/**
 * A choice in a Bayesian network. Choices can be either discrete or continuous.
 * For continuous choices the number must be made discrete by mapping it to
 * discrete ranges.
 * 
 */
public class BayesianChoice implements Serializable, Comparable<BayesianChoice> {

	/**
	 * The label for this choice.
	 */
	final private String label;

	/**
	 * The min values, if continuous, or the index if discrete.
	 */
	final double min;

	/**
	 * The max values, if continuous, or the index if discrete.
	 */
	final double max;

	/**
	 * Construct a continuous choice that covers the specified range.
	 * 
	 * @param label
	 *            The label for this choice.
	 * @param min
	 *            The minimum value for this range.
	 * @param max
	 *            The maximum value for this range.
	 */
	public BayesianChoice(String label, double min, double max) {
		super();
		this.label = label;
		this.min = min;
		this.max = max;
	}

	/**
	 * Construct a discrete choice for the specified index.
	 * 
	 * @param label
	 *            The label for this choice.
	 * @param index
	 *            The index for this choice.
	 */
	public BayesianChoice(String label, int index) {
		super();
		this.label = label;
		this.min = index;
		this.max = index;
	}

	/**
	 * @return Get the label.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return Get the min.
	 */
	public double getMin() {
		return min;
	}

	/**
	 * @return Get the max.
	 */
	public double getMax() {
		return max;
	}

	/**
	 * @return True, if this choice has an index, as opposed to min/max. If the
	 *         value has an idex, then it is discrete.
	 */
	public boolean isIndex() {
		return Math.abs(this.min - this.max) < Encog.DEFAULT_DOUBLE_EQUAL;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return this.label;
	}

	/**
	 * @return A string representation of this choice.
	 */
	public String toFullString() {
		StringBuilder result = new StringBuilder();
		result.append(this.label);
		if (!isIndex()) {
			result.append(":");
			result.append(CSVFormat.EG_FORMAT.format(this.min, 4));
			result.append(" to ");
			result.append(CSVFormat.EG_FORMAT.format(this.max, 4));
		}
		return result.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(BayesianChoice other) {
		if( this.max<other.max ) {
			return -1;
		} else {
			return 1;
		}
	}

}
