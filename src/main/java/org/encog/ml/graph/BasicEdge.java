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
