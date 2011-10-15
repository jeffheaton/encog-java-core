package org.encog.ml.graph;

import org.encog.util.Format;

public class BasicEdge {
	
	private final BasicNode from;
	private final BasicNode to;
	private final double cost;
	
	public BasicEdge(BasicNode from, BasicNode to, double cost) {
		super();
		this.from = from;
		this.to = to;
		this.cost = cost;
	}

	public BasicNode getFrom() {
		return from;
	}

	public BasicNode getTo() {
		return to;
	}

	public double getCost() {
		return cost;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[BasicConnection: from=");
		result.append(from.toString());
		result.append(",to=");
		result.append(to.toString());
		result.append(",cost=");
		result.append(Format.formatDouble(this.cost, 4));
		result.append("]");
		return result.toString();
	}
	
}
