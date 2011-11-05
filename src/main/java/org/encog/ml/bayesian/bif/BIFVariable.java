package org.encog.ml.bayesian.bif;

import java.util.ArrayList;
import java.util.List;

public class BIFVariable {
	private String name;
	private List<String> options = new ArrayList<String>();
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
	 * @return the options
	 */
	public List<String> getOptions() {
		return options;
	}
	/**
	 * @param options the options to set
	 */
	public void setOptions(List<String> options) {
		this.options = options;
	}
	public void addOption(String s) {
		this.options.add(s);
		
	}
	
	
}
