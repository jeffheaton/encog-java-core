package org.encog.ml.schedule;

import java.util.HashMap;
import java.util.Map;

import org.encog.ml.graph.BasicEdge;
import org.encog.ml.graph.BasicGraph;

public class ScheduleGraph extends BasicGraph {

	public ActionNode startNode;
	public ActionNode finishNode = new ActionNode("Finish");

	public ScheduleGraph() {
		super(new ActionNode("Start"));
		this.startNode = (ActionNode) this.getRoot();
	}

	public ActionNode addChild(ActionNode parent, String name, double duration) {
		ActionNode newNode = new ActionNode(name, duration);
		this.connect(parent, newNode, 0.0);
		return newNode;
	}

	/**
	 * @return the startNode
	 */
	public ActionNode getStartNode() {
		return startNode;
	}

	/**
	 * @return the finishNode
	 */
	public ActionNode getFinishNode() {
		return finishNode;
	}

	public void dumpNode(StringBuilder result, ActionNode node,
			Map<ActionNode, ActionNode> visited) {
		if (!visited.containsKey(node)) {
			visited.put(node, node);
			result.append(node.toString());
			result.append("\n");
			for (BasicEdge edge : node.getConnections()) {
				dumpNode(result, (ActionNode) edge.getTo(), visited);
			}
		}
	}

	public String dump() {
		StringBuilder result = new StringBuilder();
		dumpNode(result, this.getStartNode(),
				new HashMap<ActionNode, ActionNode>());
		return result.toString();
	}

}
