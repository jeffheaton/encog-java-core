package org.encog.ml.graph.search;

import org.encog.ml.graph.BasicGraph;
import org.encog.ml.graph.BasicNode;
import org.encog.ml.graph.BasicPath;
import org.encog.ml.graph.EuclideanNode;

public class AStarSearch extends AbstractGraphSearch {

	private final CostEstimator estimator;	
	
	public AStarSearch(BasicGraph theGraph, BasicNode startingPoint,
			SearchGoal theGoal, CostEstimator theEstimator) {
		super(theGraph, startingPoint, theGoal);
		this.estimator = theEstimator;
	}
	
	/**
	 * @return the estimator
	 */
	public CostEstimator getEstimator() {
		return estimator;
	}

	public double calculatePathCost(BasicPath path) {
		
		double result = 0;
		BasicNode lastNode = null;
		
		for(BasicNode node: path.getNodes()) {
			double hc = this.estimator.estimateCost(node, getGoal());
			double stepCost = 0;
			
			if( lastNode!=null ) {
				stepCost = lastNode.getCost(node);
			}
			
			result+=(hc+stepCost);
			
			lastNode = node;
		}
		
		return result;
	}


	@Override
	public boolean isHigherPriority(BasicPath first, BasicPath second) {
		double firstCost = calculatePathCost( first );
		double secondCost = calculatePathCost( second );
		return firstCost<secondCost;
	}

}
