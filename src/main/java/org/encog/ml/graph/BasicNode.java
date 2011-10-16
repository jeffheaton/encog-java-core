package org.encog.ml.graph;

import java.util.ArrayList;
import java.util.List;

import org.encog.EncogError;

public class BasicNode {
	private final List<BasicEdge> connections = new ArrayList<BasicEdge>();
	private final String label;
	
	public BasicNode(String label) {
		super();
		this.label = label;
	}

	public List<BasicEdge> getConnections() {
		return connections;
	}

	public String getLabel() {
		return label;
	}

	public void connect(BasicNode newNode,double cost) {
		this.connections.add(new BasicEdge(this,newNode,cost));
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[BasicNode: ");
		result.append(this.label);
		result.append("]");
		return result.toString();
	}

	public double getCost(BasicNode node) {
		for(BasicEdge edge: this.connections) {
			if( edge.getTo().equals(node)) {
				return edge.getCost();
			}
		}
		
		throw new EncogError("Nodes are not connected");
	}
	
}
