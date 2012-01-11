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
package org.encog.mathutil.probability.vars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.ml.bayesian.BayesianError;

public class VariableList {
	private final List<RandomVariable> variables = new ArrayList<RandomVariable>();
	private final Map<String,RandomVariable> map = new HashMap<String,RandomVariable>();
	
	public void add(RandomVariable v) {
		this.map.put(v.getLabel(), v);
		this.variables.add(v);
	}
	
	public List<RandomVariable> contents() {
		return this.variables;
	}

	public RandomVariable get(String label) {
		return this.map.get(label);
	}

	public int indexOf(RandomVariable s) {
		return this.variables.indexOf(s);
	}

	public int size() {
		return this.variables.size();
	}

	public RandomVariable get(int i) {
		return this.variables.get(i);
	}
	
	public RandomVariable requireEvent(String label) {
		RandomVariable result = this.map.get(label);
		if( result==null ) {
			throw new BayesianError("The variable " + label + " is not defined.");
		}
		return result;
	}

}
