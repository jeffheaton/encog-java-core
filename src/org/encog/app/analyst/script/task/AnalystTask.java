package org.encog.app.analyst.script.task;

import java.util.ArrayList;
import java.util.List;

public class AnalystTask {

	
	private String name;
	private final List<String> lines = new ArrayList<String>();
	
	public AnalystTask(String name) {
		this.name = name;
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
	 * @return the lines
	 */
	public List<String> getLines() {
		return lines;
	}
	
	
}
