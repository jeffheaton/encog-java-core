package org.encog.ml.world;

public interface PerformAction {
	void perform(World w, State s, Action a);
	double determineCost(World w, State s, Action a);
	boolean isPossible(World w, State s, Action a);
}
