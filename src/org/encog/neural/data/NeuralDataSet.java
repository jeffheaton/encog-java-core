package org.encog.neural.data;

public interface NeuralDataSet extends Iterable<NeuralDataPair> {
	public void add(NeuralData inputData,NeuralData idealData);
	public void add(NeuralDataPair inputData);
	public int getInputSize();
	public int getIdealSize();
}
