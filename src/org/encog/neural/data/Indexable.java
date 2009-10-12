package org.encog.neural.data;

public interface Indexable extends NeuralDataSet {
	long getRecordCount();
	void getRecord(long index, NeuralDataPair pair);
}
