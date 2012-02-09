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
package org.encog.ml.bayesian.parse;

import org.encog.Encog;

/**
 * A parsed choice.
 */
public class ParsedChoice {
	
	/**
	 * The label for this choice.
	 */
	final private String label;
	
	/**
	 * The min value for this choice.
	 */
	final double min;
	
	/**
	 * The max value for this choice.
	 */
	final double max;
	
	/**
	 * Construct a continuous choice, with a min and max.
	 * @param label The label, for this chocie.
	 * @param min The min value, for this choice.
	 * @param max The max value, for this choice.
	 */
	public ParsedChoice(String label, double min, double max) {
		super();
		this.label = label;
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Construct a discrete value for this choice.
	 * @param label The choice label.
	 * @param index The index.
	 */
	public ParsedChoice(String label, int index) {
		super();
		this.label = label;
		this.min = index;
		this.max = index;
	}

	/**
	 * @return The label.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return The min value.
	 */
	public double getMin() {
		return min;
	}

	/**
	 * @return The max value.
	 */
	public double getMax() {
		return max;
	}
	
	/**
	 * @return True, if this choice is indexed, or discrete.
	 */
	public boolean isIndex() {
		return Math.abs(this.min-this.max)<Encog.DEFAULT_DOUBLE_EQUAL;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return this.label;
	}
}
