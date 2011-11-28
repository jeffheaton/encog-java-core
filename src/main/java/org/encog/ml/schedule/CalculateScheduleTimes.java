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
