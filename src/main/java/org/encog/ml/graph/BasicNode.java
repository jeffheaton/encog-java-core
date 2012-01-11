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
package org.encog.ml.graph;

import java.util.ArrayList;
import java.util.List;

import org.encog.EncogError;

public class BasicNode {
	private final List<BasicEdge> connections = new ArrayList<BasicEdge>();
	private final List<BasicEdge> backConnections = new ArrayList<BasicEdge>();
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
		BasicEdge edge;
		this.connections.add(edge = new BasicEdge(this,newNode,cost));
		newNode.getBackConnections().add(edge);
	}
	
	/**
	 * @return the backConnections
	 */
	public List<BasicEdge> getBackConnections() {
		return backConnections;
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
