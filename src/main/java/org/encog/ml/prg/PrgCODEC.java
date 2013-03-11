package org.encog.ml.prg;

import org.encog.ml.MLMethod;
import org.encog.ml.ea.codec.GeneticCODEC;
import org.encog.ml.ea.genome.Genome;

public class PrgCODEC implements GeneticCODEC {

	@Override
	public MLMethod decode(final Genome genome) {
		return genome;
	}

	@Override
	public Genome encode(final MLMethod phenotype) {
		return (Genome) phenotype;
	}

}
