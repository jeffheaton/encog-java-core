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
