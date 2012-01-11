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
package org.encog.ml.world.basic;

import java.util.HashMap;
import java.util.Map;

import org.encog.ml.world.State;
import org.encog.util.EngineArray;
import org.encog.util.Format;

public class BasicState implements State {

	private final Map<String, Object> properties = new HashMap<String, Object>();
	private double reward;
	private double[] policyValues;
	private int visited;
	
	@Override
	public void setProperty(String key, Object value) {
		this.properties.put(key, value);		
	}

	@Override
	public Object getProperty(String key) {
		return this.properties.get(key);
	}

	@Override
	public double getReward() {
		return this.reward;
	}
	
	@Override
	public void setReward(double r) {
		this.reward = r;
	}
	
	@Override
	public double []getPolicyValue() {
		return this.policyValues;
	}
	
	@Override
	public void setAllPolicyValues(double d) {
		EngineArray.fill(this.policyValues, d);
	}
	
	@Override
	public void setPolicyValueSize(int s) {
		this.policyValues = new double[s];
	}
	
	@Override
	public boolean wasVisited() {
		return this.visited>0;
	}
	
	@Override
	public void setVisited(int i) {
		this.visited = i;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[BasicState: ");
		for(int i=0;i<this.policyValues.length;i++) {
			result.append(Format.formatDouble(getPolicyValue()[i], 4));
			result.append(" ");
		}
		result.append("]");
		return result.toString();
	}

	@Override
	public int getVisited() {
		return this.visited;		
	}

	@Override
	public void increaseVisited() {
		this.visited++;		
	}
	
}
