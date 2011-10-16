package org.encog.ml.graph.search;

import org.encog.ml.graph.BasicNode;
import org.encog.ml.graph.BasicPath;
import org.encog.ml.graph.EuclideanNode;

public class SimpleDestinationGoal implements SearchGoal {

	private final BasicNode goalDestination;
		
	public SimpleDestinationGoal(BasicNode goalDestination) {
		super();
		this.goalDestination = goalDestination;
	}

	@Override
	public boolean isGoalMet(BasicPath path) {
		if( path.getDestinationNode() == goalDestination ) {
			return true;
		}
		return false;
	}

	/**
	 * @return the goalDestination
	 */
	public BasicNode getGoalDestination() {
		return goalDestination;
	}

	

}
