package org.encog.neural.data;

public interface NeuralDataSet {
	public void add(NeuralData inputData,NeuralData idealData);
	public void add(NeuralData inputData);
	public NeuralData getInput(int index);
	public NeuralData getIdeal(int index);
	public int size();
}
