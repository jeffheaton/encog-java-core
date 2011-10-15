package org.encog.ml.graph.search;

import org.encog.ml.graph.BasicGraph;
import org.encog.ml.graph.BasicNode;
import org.encog.ml.graph.BasicPath;

public class BreadthFirstSearch extends AbstractGraphSearch {

	public BreadthFirstSearch(BasicGraph theGraph, BasicNode startingPoint,
			SearchGoal theGoal) {
		super(theGraph, startingPoint, theGoal);
	}

	@Override
	public boolean isHigherPriority(BasicPath first, BasicPath second) {
		return first.size()<second.size();
	}

}
