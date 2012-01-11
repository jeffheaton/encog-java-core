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

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.encog.Encog;
import org.encog.ml.world.Action;
import org.encog.ml.world.State;
import org.encog.ml.world.SuccessorState;
import org.encog.ml.world.WorldError;
import org.encog.ml.world.grid.GridState;
import org.encog.ml.world.grid.GridWorld;

public class GridStochasticProbability extends GridAbstractProbability {

	private double probabilitySuccess;
	private double probabilitySame;
	private double probabilityLeft;
	private double probabilityRight;
	private double probabilityReverse;

	public GridStochasticProbability(GridWorld theWorld,
			double theProbabilitySuccess, double theProbabilitySame,
			double theProbabilityLeft, double theProbabilityRight,
			double theProbabilityReverse) {
		super(theWorld);
		this.probabilitySuccess = theProbabilitySuccess;
		this.probabilitySame = theProbabilitySame;
		this.probabilityLeft = theProbabilityLeft;
		this.probabilityRight = theProbabilityRight;
		this.probabilityReverse = theProbabilityReverse;
	}

	public GridStochasticProbability(GridWorld theWorld) {
		this(theWorld, 0.8, 0.0, 0.1, 0.1, 0.0);
	}

	/**
	 * @return the probabilitySuccess
	 */
	public double getProbabilitySuccess() {
		return probabilitySuccess;
	}

	/**
	 * @param probabilitySuccess the probabilitySuccess to set
	 */
	public void setProbabilitySuccess(double probabilitySuccess) {
		this.probabilitySuccess = probabilitySuccess;
	}

	/**
	 * @return the probabilitySame
	 */
	public double getProbabilitySame() {
		return probabilitySame;
	}

	/**
	 * @param probabilitySame the probabilitySame to set
	 */
	public void setProbabilitySame(double probabilitySame) {
		this.probabilitySame = probabilitySame;
	}

	/**
	 * @return the probabilityLeft
	 */
	public double getProbabilityLeft() {
		return probabilityLeft;
	}

	/**
	 * @param probabilityLeft the probabilityLeft to set
	 */
	public void setProbabilityLeft(double probabilityLeft) {
		this.probabilityLeft = probabilityLeft;
	}

	/**
	 * @return the probabilityRight
	 */
	public double getProbabilityRight() {
		return probabilityRight;
	}

	/**
	 * @param probabilityRight the probabilityRight to set
	 */
	public void setProbabilityRight(double probabilityRight) {
		this.probabilityRight = probabilityRight;
	}

	/**
	 * @return the probabilityReverse
	 */
	public double getProbabilityReverse() {
		return probabilityReverse;
	}

	/**
	 * @param probabilityReverse the probabilityReverse to set
	 */
	public void setProbabilityReverse(double probabilityReverse) {
		this.probabilityReverse = probabilityReverse;
	}

	@Override
	public double calculate(State resultState, State previousState,
			Action desiredAction) {
		if (!(resultState instanceof GridState)
				|| !(previousState instanceof GridState)) {
			throw new WorldError("Must be instance of GridState");
		}

		GridState gridResultState = (GridState) resultState;
		GridState gridPreviousState = (GridState) previousState;

		Action resultingAction = determineResultingAction(gridPreviousState,
				gridResultState);
		GridState desiredState = determineActionState(gridPreviousState,
				desiredAction);

		// are we trying to move nowhere
		if (gridResultState == gridPreviousState) {
			if (GridWorld.isStateBlocked(desiredState))
				return this.probabilitySuccess;
			else
				return 0.0;
		}

		if (resultingAction == desiredAction) {
			return this.probabilitySuccess;
		} else if (resultingAction == GridWorld.rightOfAction(desiredAction)) {
			return this.probabilityRight;
		} else if (resultingAction == GridWorld.leftOfAction(desiredAction)) {
			return this.probabilityLeft;
		} else if (resultingAction == GridWorld.reverseOfAction(desiredAction)) {
			return this.probabilityReverse;
		} else {
			return 0.0;
		}
	}

	@Override
	public Set<SuccessorState> determineSuccessorStates(State state,
			Action action) {

		Set<SuccessorState> result = new TreeSet<SuccessorState>();

		if (action != null) {
			// probability of successful action
			if (this.probabilitySuccess > Encog.DEFAULT_DOUBLE_EQUAL) {
				State newState = determineActionState((GridState) state, action);
				if( newState!=null )
					result.add(new SuccessorState(newState, this.probabilitySuccess));
			}

			// probability of left
			if (this.probabilityLeft > Encog.DEFAULT_DOUBLE_EQUAL) {
				State newState = determineActionState((GridState) state,
						GridWorld.leftOfAction(action));
				if( newState!=null )
					result.add(new SuccessorState(newState, this.probabilityLeft));
			}

			// probability of right
			if (this.probabilityRight > Encog.DEFAULT_DOUBLE_EQUAL) {
				State newState = determineActionState((GridState) state,
						GridWorld.rightOfAction(action));
				if( newState!=null )
					result.add(new SuccessorState(newState, this.probabilityRight));
			}

			// probability of reverse
			if (this.probabilityReverse > Encog.DEFAULT_DOUBLE_EQUAL) {
				State newState = determineActionState((GridState) state,
						GridWorld.reverseOfAction(action));
				if( newState!=null )
					result.add(new SuccessorState(newState, this.probabilityReverse));
			}
		}

		return result;
	}
}
