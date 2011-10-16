package org.encog.ml.graph;

import junit.framework.TestCase;

import org.encog.ml.graph.search.BreadthFirstSearch;
import org.encog.ml.graph.search.DepthFirstSearch;
import org.encog.ml.graph.search.SimpleDestinationGoal;

public class TestSearch extends TestCase {
	
	public void testDepthFirstSearch() {
		BasicNode nodeA = new BasicNode("a");
		BasicGraph graph = new BasicGraph(nodeA);
		BasicNode nodeB = graph.connect(nodeA,new BasicNode("b"),5);
		BasicNode nodeC = graph.connect(nodeA,new BasicNode("c"),2);
		BasicNode nodeD = graph.connect(nodeB, new BasicNode("d"), 1);
		nodeC.connect(nodeD, 1);
		
		DepthFirstSearch search = new DepthFirstSearch(graph,nodeA,new SimpleDestinationGoal(nodeD));
		while( search.getSolution()==null ) {
			search.iteration();
		}
		
		BasicPath solution = search.getSolution();
		System.out.println(solution.toString());
	}
	
	public void testBredthFirstSearch() {
		BasicNode nodeA = new BasicNode("a");
		BasicGraph graph = new BasicGraph(nodeA);
		BasicNode nodeB = graph.connect(nodeA,new BasicNode("b"),5);
		BasicNode nodeC = graph.connect(nodeA,new BasicNode("c"),2);
		BasicNode nodeD = graph.connect(nodeB, new BasicNode("d"), 1);
		nodeC.connect(nodeD, 1);
		
		BreadthFirstSearch search = new BreadthFirstSearch(graph,nodeA,new SimpleDestinationGoal(nodeD));
		while( search.getSolution()==null ) {
			search.iteration();
		}
		
		BasicPath solution = search.getSolution();
		System.out.println(solution.toString());
	}
}
