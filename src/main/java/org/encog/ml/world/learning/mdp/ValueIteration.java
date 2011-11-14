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
