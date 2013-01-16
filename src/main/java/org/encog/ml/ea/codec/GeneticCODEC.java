package org.encog.ml.ea.codec;

import org.encog.ml.MLMethod;
import org.encog.ml.ea.genome.Genome;

public interface GeneticCODEC {
	MLMethod decode(Genome genome);
	Genome encode(MLMethod phenotype);
}
