/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.graph.search.AStarSearch;
import org.encog.ml.graph.search.BreadthFirstSearch;
import org.encog.ml.graph.search.DepthFirstSearch;
import org.encog.ml.graph.search.EuclideanCostEstimator;
import org.encog.ml.graph.search.GraphSearch;
import org.encog.ml.graph.search.SimpleDestinationGoal;

public class TestSearch extends TestCase {
	
	private int countIterations(GraphSearch search) {
		int result = 0;
		while( search.getSolution()==null ) {
			search.iteration();
			result++;
		}
		return result;
	}
	
	public void testDepthFirstSearch() {
		BasicNode nodeA = new BasicNode("a");
		BasicGraph graph = new BasicGraph(nodeA);
		BasicNode nodeB = graph.connect(nodeA,new BasicNode("b"),5);
		BasicNode nodeC = graph.connect(nodeA,new BasicNode("c"),2);
		BasicNode nodeD = graph.connect(nodeB, new BasicNode("d"), 1);
		nodeC.connect(nodeD, 1);
		
		DepthFirstSearch search = new DepthFirstSearch(graph,nodeA,new SimpleDestinationGoal(nodeD));
		Assert.assertEquals(3, countIterations(search));
		
		BasicPath solution = search.getSolution();
		Assert.assertEquals(solution.getNodes().get(0).getLabel(), "a");
		Assert.assertEquals(solution.getNodes().get(1).getLabel(), "b");
		Assert.assertEquals(solution.getNodes().get(2).getLabel(), "d");
	}
	
	public void testBredthFirstSearch() {
		BasicNode nodeA = new BasicNode("a");
		BasicGraph graph = new BasicGraph(nodeA);
		BasicNode nodeB = graph.connect(nodeA,new BasicNode("b"),5);
		BasicNode nodeC = graph.connect(nodeA,new BasicNode("c"),2);
		BasicNode nodeD = graph.connect(nodeB, new BasicNode("d"), 1);
		nodeC.connect(nodeD, 1);
		
		BreadthFirstSearch search = new BreadthFirstSearch(graph,nodeA,new SimpleDestinationGoal(nodeD));
		Assert.assertEquals(4, countIterations(search));
		
		BasicPath solution = search.getSolution();
		Assert.assertEquals(solution.getNodes().get(0).getLabel(), "a");
		Assert.assertEquals(solution.getNodes().get(1).getLabel(), "b");
		Assert.assertEquals(solution.getNodes().get(2).getLabel(), "d");
	}
	
	public void testAStar() {
		EuclideanNode nodeA = new EuclideanNode("a", 0, 0);
		BasicGraph graph = new BasicGraph(nodeA);
		BasicNode nodeB = graph.connect(nodeA, new EuclideanNode("b",0,2), 10.0);
		BasicNode nodeC = graph.connect(nodeA, new EuclideanNode("c",0,1), 1.0);
		BasicNode nodeD = graph.connect(nodeB, new EuclideanNode("d",0,1), 1.0);
		nodeD.connect(nodeD, 1.0);
		nodeC.connect(nodeD, 1.0);
		
		AStarSearch search = new AStarSearch(
				graph,
				nodeA,
				new SimpleDestinationGoal(nodeD),
				new EuclideanCostEstimator());
		Assert.assertEquals(3, countIterations(search));
		
		BasicPath solution = search.getSolution();
		Assert.assertEquals(solution.getNodes().get(0).getLabel(), "a");
		Assert.assertEquals(solution.getNodes().get(1).getLabel(), "c");
		Assert.assertEquals(solution.getNodes().get(2).getLabel(), "d");
	}
}
