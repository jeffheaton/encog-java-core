package org.encog.ml.genetic.genome;

import org.encog.ml.ea.genome.Genome;

public interface ArrayGenome extends Genome {

	void copy(ArrayGenome source, int sourceIndex, int targetIndex);

	void swap(int iswap1, int iswap2);

}
