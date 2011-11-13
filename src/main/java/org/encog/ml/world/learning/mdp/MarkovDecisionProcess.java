package org.encog.ml.world.learning.mdp;

import org.encog.ml.world.State;
import org.encog.ml.world.World;

public class MarkovDecisionProcess {
		
	private final World world; 
	private final State goal;
	
	public MarkovDecisionProcess(World theWorld, State theGoal) {
		this.world = theWorld;
		this.goal = theGoal;
	}

	/**
	 * @return the world
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * @return the goal
	 */
	public State getGoal() {
		return goal;
	}
	
	
}
