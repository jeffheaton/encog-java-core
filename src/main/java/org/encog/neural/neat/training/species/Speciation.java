package org.encog.neural.neat.training.species;

import org.encog.neural.neat.NEATSpecies;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATTraining;

public interface Speciation {

	void init(NEATTraining theOwner);
	void addChild(NEATGenome genome);
	void performSpeciation();
	void ageSpecies(NEATSpecies species);

}
