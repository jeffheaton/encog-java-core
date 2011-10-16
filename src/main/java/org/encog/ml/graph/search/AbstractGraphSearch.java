package org.encog.ml.graph.search;

import java.util.HashSet;
import java.util.Set;

import org.encog.EncogError;
import org.encog.ml.graph.BasicEdge;
import org.encog.ml.graph.BasicGraph;
import org.encog.ml.graph.BasicNode;
import org.encog.ml.graph.BasicPath;

public abstract class AbstractGraphSearch implements Prioritizer {
	
	private final BasicGraph graph;
	private final SearchGoal goal;
	private final FrontierHolder frontier = new FrontierHolder(this);
	private final Set<BasicNode> explored = new HashSet<BasicNode>();
	private BasicPath solution;

	public AbstractGraphSearch(BasicGraph theGraph, BasicNode startingPoint, SearchGoal theGoal)
	{
		this.graph = theGraph;
		this.goal = theGoal;
		frontier.add(new BasicPath(startingPoint));
	}

	public BasicGraph getGraph() {
		return graph;
	}

	public SearchGoal getGoal() {
		return goal;
	}
	
	public void iteration() {
		if (solution == null) {
					
			if( this.frontier.size()==0 ) {
				throw new EncogError("Frontier is empty, cannot find solution.");
			}
			
			BasicPath path = this.frontier.pop();

			if (this.goal.isGoalMet(path)) {
				this.solution = path;
				return;
			}

			BasicNode state = path.getDestinationNode();
			this.explored.add(state);
			
			for (BasicEdge connection : state.getConnections()) {
				if( !this.explored.contains(connection.getTo()) &&
					!this.frontier.containsDestination(connection.getTo())) {
					BasicPath path2 = new BasicPath(path, connection.getTo());
					this.frontier.add(path2);
				}
			}
		}
	}

	/**
	 * @return the solution
	 */
	public BasicPath getSolution() {
		return solution;
	}
	
	

}
