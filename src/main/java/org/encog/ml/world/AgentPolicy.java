package org.encog.ml.world;

public interface AgentPolicy {
	Action determineNextAction(WorldAgent agent);
	World getWorld();
}
