package org.encog.ml.schedule;

import org.encog.ml.graph.BasicNode;
import org.encog.util.Format;

public class ActionNode extends BasicNode {
	private double duration;
	private double earliestStartTime;
	private double latestStartTime;
	
	public ActionNode(String label) {
		super(label);		
	}

	public ActionNode(String theName, double theDuration) {
		super(theName);
		this.duration = theDuration;
	}

	/**
	 * @return the earliestStartTime
	 */
	public double getEarliestStartTime() {
		return earliestStartTime;
	}

	/**
	 * @param earliestStartTime the earliestStartTime to set
	 */
	public void setEarliestStartTime(double earliestStartTime) {
		this.earliestStartTime = earliestStartTime;
	}

	/**
	 * @return the latestStartTime
	 */
	public double getLatestStartTime() {
		return latestStartTime;
	}

	/**
	 * @param latestStartTime the latestStartTime to set
	 */
	public void setLatestStartTime(double latestStartTime) {
		this.latestStartTime = latestStartTime;
	}

	/**
	 * @return the duration
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(double duration) {
		this.duration = duration;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[ActionNode: label=");
		result.append(this.getLabel());
		result.append("; earliestStartTime=");
		result.append(Format.formatDouble(this.earliestStartTime, 4));
		result.append("; latestStartTime=");
		result.append(Format.formatDouble(this.latestStartTime, 4));
		result.append("]");
		return result.toString();
	}
	

}
