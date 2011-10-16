package org.encog.ml.graph;

import java.util.ArrayList;
import java.util.List;

public class BasicPath {
	
	private final List<BasicNode> nodes = new ArrayList<BasicNode>();
	
	public BasicPath(BasicNode startingPoint) {
		this.nodes.add(startingPoint);
	}

	public BasicPath(BasicPath path, BasicNode newNode) {
		this.nodes.addAll(path.getNodes());
		this.nodes.add(newNode);
	}

	public List<BasicNode> getNodes() {
		return nodes;
	}

	public BasicNode getDestinationNode() {
		if( this.nodes.size()==0)
			return null;
		return this.nodes.get(this.nodes.size()-1);
	}

	public int size() {
		return this.nodes.size();
	}
	
	public String toString() {
		boolean first = true;
		StringBuilder result = new StringBuilder();
		result.append("[BasicPath: ");
		for(BasicNode node: this.nodes) {
			if( !first ) {
				result.append(',');
			}
			result.append(node.toString());
			first = false;
		}
		result.append("]");
		return result.toString();
	}
	
}
