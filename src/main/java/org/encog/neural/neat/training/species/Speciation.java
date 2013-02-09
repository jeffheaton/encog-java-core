package org.encog.neural.neat.training.species;

import org.encog.neural.neat.training.NEATTraining;
import org.encog.neural.neat.training.NEATGenome;

public interface Speciation {

	void init(NEATTraining theOwner);
	void addChild(NEATGenome genome);
	void performSpeciation();

}
