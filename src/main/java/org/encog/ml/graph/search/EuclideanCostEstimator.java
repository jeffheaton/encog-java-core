package org.encog.ml.graph.search;

import org.encog.EncogError;
import org.encog.ml.graph.BasicNode;
import org.encog.ml.graph.EuclideanNode;

public class EuclideanCostEstimator implements CostEstimator {

	@Override
	public double estimateCost(BasicNode startingNode, SearchGoal goal) {
		
		if( !(startingNode instanceof EuclideanNode) ) {
			throw new EncogError("Starting node must be EuclideanNode.");
		}
		
		if( !(goal instanceof SimpleDestinationGoal) ) {
			throw new EncogError("Goal must be SimpleDistanceGoal.");
		}
		
		SimpleDestinationGoal sdg = (SimpleDestinationGoal)goal;
		EuclideanNode endingNode = (EuclideanNode)sdg.getGoalDestination();
		
		return EuclideanNode.distance(
				(EuclideanNode)startingNode, 
				endingNode);
	}

}
