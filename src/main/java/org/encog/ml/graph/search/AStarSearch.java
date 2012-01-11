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
