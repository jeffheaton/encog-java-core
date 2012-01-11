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

public class BayesianChoice implements Serializable {
	
	final private String label;
	final double min;
	final double max;
	
	public BayesianChoice(String label, double min, double max) {
		super();
		this.label = label;
		this.min = min;
		this.max = max;
	}
	
	public BayesianChoice(String label, int index) {
		super();
		this.label = label;
		this.min = index;
		this.max = index;
	}

	public String getLabel() {
		return label;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}
	
	public boolean isIndex() {
		return Math.abs(this.min-this.max)<Encog.DEFAULT_DOUBLE_EQUAL;
	}
	
	public String toString() {
		return this.label;
	}
	
	public String toFullString() {
		StringBuilder result = new StringBuilder();
		result.append(this.label);
		if( !isIndex() ) {
			result.append(":");
			result.append(CSVFormat.EG_FORMAT.format(this.min, 4));
			result.append(" to ");
			result.append(CSVFormat.EG_FORMAT.format(this.max, 4));
		}
		return result.toString();
	}
		
}
