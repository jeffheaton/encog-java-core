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
