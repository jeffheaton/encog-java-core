package org.encog.neural.neat.training.opp.links;

import java.util.Random;

import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATTraining;

public interface MutateLinkWeight {

	void init(NEATTraining theTrainer);

	NEATTraining getTrainer();

	void mutateWeight(Random rnd, NEATLinkGene linkGene, double weightRange);

}
