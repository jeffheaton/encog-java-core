package org.encog.ml.genetic.genome;

public interface ArrayGenome extends Genome {

	void copy(ArrayGenome source, int sourceIndex, int targetIndex);

	void swap(int iswap1, int iswap2);

}
