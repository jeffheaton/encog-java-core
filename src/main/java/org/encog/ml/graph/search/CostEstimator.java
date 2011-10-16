package org.encog.ml.graph.search;

import org.encog.ml.graph.BasicNode;

public interface CostEstimator {
	double estimateCost(BasicNode startingNode, SearchGoal goal);
}
