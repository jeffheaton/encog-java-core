package org.encog.neural.neat;

import java.util.List;
import java.util.Random;

import org.encog.ml.ea.genome.GenomeFactory;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;

public interface NEATGenomeFactory extends GenomeFactory {
	NEATGenome factor(long genomeID, List<NEATNeuronGene> neurons,
			List<NEATLinkGene> links, int inputCount, int outputCount);

	NEATGenome factor(NEATGenome other);

	NEATGenome factor(Random rnd, NEATPopulation pop, long id, int inputCount,
			int outputCount, double connectionDensity);
}
