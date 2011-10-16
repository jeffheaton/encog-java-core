package org.encog.ml.graph;

import java.util.ArrayList;
import java.util.List;

public class BasicGraph {
	
	private final List<BasicNode> nodes = new ArrayList<BasicNode>();
	private final BasicNode root;
	
	public BasicGraph(BasicNode rootNode) {
		this.root = rootNode;
		nodes.add(rootNode);
	}

	public List<BasicNode> getNodes() {
		return this.nodes;
	}

	/**
	 * @return the root
	 */
	public BasicNode getRoot() {
		return root;
	}

	public BasicNode connect(BasicNode baseNode, BasicNode newNode, double cost) {
		this.nodes.add(newNode);
		baseNode.connect(newNode,cost);
		return newNode;
	}
	
	
	
}
