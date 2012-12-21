package org.encog.ml.genetic.genome;

public interface ArrayGenome {

	int size();

	void copy(ArrayGenome source, int sourceIndex, int targetIndex);

	void copy(ArrayGenome parent);

	void swap(int iswap1, int iswap2);

}
