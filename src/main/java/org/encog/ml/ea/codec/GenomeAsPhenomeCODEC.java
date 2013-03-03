package org.encog.ml.ea.codec;

import org.encog.ml.MLMethod;
import org.encog.ml.ea.genome.Genome;

public class GenomeAsPhenomeCODEC implements GeneticCODEC {

	@Override
	public MLMethod decode(Genome genome) {
		return (MLMethod) genome;
	}

	@Override
	public Genome encode(MLMethod phenotype) {
		return (Genome) phenotype;
	}

}
