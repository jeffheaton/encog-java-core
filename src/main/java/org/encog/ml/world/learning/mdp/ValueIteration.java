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
package org.encog.ml.world.learning.mdp;

import org.encog.Encog;
import org.encog.ml.world.Action;
import org.encog.ml.world.State;
import org.encog.ml.world.SuccessorState;
import org.encog.ml.world.World;

public class ValueIteration extends MarkovDecisionProcess {

	private double discountFactor;
	
	public ValueIteration(World theWorld, double theDiscountFactor) {
		super(theWorld);	
		this.discountFactor = theDiscountFactor;
	}
	
	public void calculateValue(State state) {
		double result = Double.NEGATIVE_INFINITY;
		if (!getWorld().isGoalState(state) ) {
			for (Action action : getWorld().getActions()) {
				double sum = 0;
				for (SuccessorState statePrime : this.getWorld()
						.getProbability()
						.determineSuccessorStates(state, action)) {
					sum += statePrime.getProbability()
							* statePrime.getState().getPolicyValue()[0];
				}
				sum *= this.discountFactor;

				result = Math.max(result, sum);
			}

			state.getPolicyValue()[0] = result+state.getReward();
		} else {
			state.getPolicyValue()[0] = state.getReward();
		}
	}

	public void iteration() {
		for(State state: getWorld().getStates() ) {
			calculateValue(state);
		}
	}

}
