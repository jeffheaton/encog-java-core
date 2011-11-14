package org.encog.ml.world.learning.mdp;

import org.encog.ml.world.State;
import org.encog.ml.world.World;
import org.encog.ml.world.WorldError;

public class MarkovDecisionProcess {
		
	private final World world; 
	private final State goal;
	
	public MarkovDecisionProcess(World theWorld) {
		this.world = theWorld;
		this.goal = theWorld.getGoals().get(0);
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
