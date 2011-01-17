package org.encog.ml;

import org.encog.neural.data.NeuralData;

public interface MLRegression extends MLMethod {
	int getInputCount();
	int getOutputCount();
	NeuralData compute(NeuralData input);
}
