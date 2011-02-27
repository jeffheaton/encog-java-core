package org.encog.ml.factory.parse;

import java.util.HashMap;
import java.util.Map;

public class ArchitectureLayer {
	private boolean bias;
	private int count;
	private String name;
	private final Map<String,String> params = new HashMap<String,String>();
	/**
	 * @return the bias
	 */
	public boolean isBias() {
		return bias;
	}
	/**
	 * @param bias the bias to set
	 */
	public void setBias(boolean bias) {
		this.bias = bias;
	}
	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the params
	 */
	public Map<String, String> getParams() {
		return params;
	}
	
	
}
