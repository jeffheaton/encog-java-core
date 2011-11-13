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
