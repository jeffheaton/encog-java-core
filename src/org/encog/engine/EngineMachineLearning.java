package org.encog.engine;

public interface EngineMachineLearning {
	
	int getOutputCount();
	int getInputCount();
	void compute(double[] input, double[] output);
	void computeSparse(double[] input, boolean provided[], double[] output);

}
