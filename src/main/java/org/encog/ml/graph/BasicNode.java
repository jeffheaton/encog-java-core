package org.encog.ml.graph;

import java.util.ArrayList;
import java.util.List;

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
	
	
	
}
