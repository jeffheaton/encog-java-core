package org.encog.ml.prg;

import org.encog.ml.MLMethod;
import org.encog.ml.ea.codec.GeneticCODEC;
import org.encog.ml.ea.genome.Genome;

/**
 * Encode and decode an Encog program between genome and phenotypes. This is a
 * passthrough, as the Encog geneome and phenome are identical.
 */
public class PrgCODEC implements GeneticCODEC {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLMethod decode(final Genome genome) {
		return genome;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Genome encode(final MLMethod phenotype) {
		return (Genome) phenotype;
	}

}
