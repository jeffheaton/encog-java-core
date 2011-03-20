package org.encog.ml;

import org.encog.neural.data.NeuralDataSet;

public interface MLError extends MLMethod {
	double calculateError(final NeuralDataSet data);
}
