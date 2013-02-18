package org.encog.neural.neat.training.species;

import org.encog.neural.neat.training.NEATTraining;

public interface Speciation {

	void init(NEATTraining theOwner);
	void performSpeciation();
}
