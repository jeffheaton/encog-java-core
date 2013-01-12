package org.encog.neural.neat;

import java.util.List;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;

public interface NEATGenomeFactory extends GenomeFactory {
	NEATGenome factor(NEATGenome other);

	NEATGenome factor(final long genomeID,
			final List<NEATNeuronGene> neurons, final List<NEATLinkGene> links,
			final int inputCount, final int outputCount);

	NEATGenome factor(final NEATPopulation pop, final long id,
			final int inputCount, final int outputCount);
}
