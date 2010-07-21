package org.encog.engine;

public interface EngineNeuralNetwork extends EngineMachineLearning {
	int getEncodeLength();
	double[] encodeNetwork();
	void decodeNetwork(double[] data);
}
