package org.encog.ml.world.learning.mdp;

import org.encog.ml.world.Action;
import org.encog.ml.world.State;
import org.encog.ml.world.SuccessorState;
import org.encog.ml.world.World;
import org.encog.ml.world.grid.GridState;

public class ValueIteration extends MarkovDecisionProcess {

	private double discountFactor;
	
	public ValueIteration(World theWorld, State theGoal, double theDiscountFactor) {
		super(theWorld, theGoal);		
	}
	
	public void calculateValue(GridState state) {
		double result = Double.NEGATIVE_INFINITY;
		
		for(Action action : getWorld().getActions()) {			
			double sum = 0;
			for(SuccessorState statePrime : this.getWorld().getProbability().determineSuccessorStates(state, action) ) {
				sum+=statePrime.getProbability()*statePrime.getState().getPolicyValue()[0];
			}
			sum*=this.discountFactor;
			sum+=state.getReward();
			
			result = Math.max(result, sum);
		}
		
		state.getPolicyValue()[0] = result;
	}

}
