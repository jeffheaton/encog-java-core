package org.encog.neural.neat.training.opp.links;

import java.util.List;
import java.util.Random;

import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATTraining;

public interface SelectLinks {

	void init(NEATTraining theTrainer);

	List<NEATLinkGene> selectLinks(Random rnd, NEATGenome genome);

	NEATTraining getTrainer();

}
