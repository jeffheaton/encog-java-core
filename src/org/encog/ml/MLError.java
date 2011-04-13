package org.encog.ml;

import org.encog.neural.data.NeuralDataSet;

/**
 * Defines Machine Learning Method that can calculate an error based on a 
 * data set.
 *
 */
public interface MLError extends MLMethod {
	double calculateError(final NeuralDataSet data);
}
