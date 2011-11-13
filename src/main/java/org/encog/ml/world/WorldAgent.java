package org.encog.ml.world;

public interface WorldAgent {
	State getCurrentState();
	void setCurrentState(State s);
	AgentPolicy getPolicy();
	void setAgentPolicy(AgentPolicy policy);
	void tick();
	World getWorld();
	void setWorld(World world);
}
