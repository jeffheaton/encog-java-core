package org.encog.neural.data;

public interface Indexable {
	long getRecordCount();
	void getRecord(int index, NeuralDataPair pair);
}
