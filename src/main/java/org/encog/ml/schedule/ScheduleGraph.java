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
