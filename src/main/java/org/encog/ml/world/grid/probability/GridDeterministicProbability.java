package org.encog.ml.world.grid.probability;

import java.util.Set;
import java.util.TreeSet;

import org.encog.ml.world.Action;
import org.encog.ml.world.State;
import org.encog.ml.world.SuccessorState;
import org.encog.ml.world.WorldError;
import org.encog.ml.world.grid.GridState;
import org.encog.ml.world.grid.GridWorld;

public class GridDeterministicProbability extends GridAbstractProbability {
	
	public GridDeterministicProbability(GridWorld theWorld) {
		super(theWorld);
	}

	@Override
	public double calculate(State resultState, State previousState, Action desiredAction) {
		
		if( !(resultState instanceof GridState) || !(previousState instanceof GridState) ) {
			throw new WorldError("Must be instance of GridState");
		}
		
		GridState gridResultState = (GridState)resultState;
		GridState gridPreviousState = (GridState)previousState;
				
		Action resultingAction =  determineResultingAction(gridPreviousState, gridResultState);
		GridState desiredState = determineActionState(gridPreviousState,desiredAction);
		
		// are we trying to move nowhere
		if( gridResultState==gridPreviousState ) {
			if( GridWorld.isStateBlocked(desiredState) )
				return 1.0;
			else
				return 0.0;
		}
		
		if( resultingAction!=null )
			return 1.0;
		else
			return 0.0;
	}

	@Override
	public Set<SuccessorState> determineSuccessorStates(State state,
			Action action) {
		
		Set<SuccessorState> result = new TreeSet<SuccessorState>();		
		if(action!=null) {
			State newState = determineActionState((GridState)state, action);
			result.add(new SuccessorState(newState,1.0));
		}
		return result;
	}

}
