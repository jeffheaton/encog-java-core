package org.encog.ml.world.grid.probability;

import org.encog.ml.world.Action;
import org.encog.ml.world.ActionProbability;
import org.encog.ml.world.grid.GridState;
import org.encog.ml.world.grid.GridWorld;

public abstract class GridAbstractProbability implements ActionProbability {
	
	private final GridWorld world;
	
	public GridAbstractProbability(GridWorld theWorld) {
		this.world = theWorld;
	}
	
	public Action determineResultingAction(GridState s1, GridState s2) {
		
		if( (s1.getRow()-1)==s2.getRow() && s1.getColumn()==s2.getColumn() ) {
			return GridWorld.ACTION_NORTH;
		} else if( (s1.getRow()==s2.getRow()+1) && s1.getColumn()==s2.getColumn() ) {
			return GridWorld.ACTION_SOUTH;
		} else if( s1.getRow()==s2.getRow() && (s1.getColumn()+1)==s2.getColumn() ) {
			return GridWorld.ACTION_EAST;
		} else if( s1.getRow()==s2.getRow() && (s1.getColumn()-1)==s2.getColumn() ) {
			return GridWorld.ACTION_EAST;
		}
				
		return null;
	}
	
	public GridState determineActionState(GridState currentState, Action action) {
		
		GridState result = null;
		
		if( action==GridWorld.ACTION_NORTH) {
			result = this.world.getState(currentState.getRow()-1, currentState.getColumn());
		} else if( action==GridWorld.ACTION_SOUTH) {
			result = this.world.getState(currentState.getRow()+1, currentState.getColumn());
		} else if( action==GridWorld.ACTION_EAST) {
			result = this.world.getState(currentState.getRow(), currentState.getColumn()+1);
		} else if( action==GridWorld.ACTION_WEST) {
			result = this.world.getState(currentState.getRow(), currentState.getColumn()-1);
		}
		
		if( result==null ) {
			result = currentState;
		}
		
		return result;
	}

	/**
	 * @return the world
	 */
	public GridWorld getWorld() {
		return world;
	}
	
	
}
