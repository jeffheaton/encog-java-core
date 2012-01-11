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

import org.encog.ml.graph.BasicEdge;
import org.encog.ml.world.basic.BasicAction;

public class CalculateScheduleTimes {
	
	public void forward(ActionNode node) {
		
		// find the max
		double m = Double.NEGATIVE_INFINITY;
		for(BasicEdge edge: node.getBackConnections()) {
			double d = ((ActionNode)edge.getFrom()).getEarliestStartTime()+((ActionNode)edge.getFrom()).getDuration();
			m = Math.max(d, m);
		}
		node.setEarliestStartTime(m);
		
		// handle other nodes
		for(BasicEdge edge: node.getConnections()) {
			forward((ActionNode)edge.getTo());
		}
	}
	
	public void backward(ActionNode node) {
		// find the min
		double m = Double.POSITIVE_INFINITY;
		for(BasicEdge edge: node.getConnections()) {
			double d = ((ActionNode)edge.getTo()).getLatestStartTime()-((ActionNode)edge.getFrom()).getDuration();
			m = Math.min(d, m);
		}
		node.setLatestStartTime(m);
		
		// handle other nodes
		for(BasicEdge edge: node.getBackConnections()) {
			backward((ActionNode)edge.getFrom());
		}	
	}
	
	public void calculate(ScheduleGraph graph) {
		// forward pass
		graph.getStartNode().setEarliestStartTime(0);
		for(BasicEdge edge: graph.getStartNode().getConnections())  {
			forward((ActionNode)edge.getTo());
		}
		
		// backward
		graph.getFinishNode().setLatestStartTime(graph.getFinishNode().getEarliestStartTime());
		for(BasicEdge edge: graph.getFinishNode().getBackConnections())  {
			backward((ActionNode)edge.getFrom());
		}
	}
}
