package org.encog.util.error;

import org.encog.engine.util.ErrorCalculation;
import org.encog.ml.MLContext;
import org.encog.ml.MLRegression;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;

public class CalculateRegressionError {
	
	public static double calculateError(final MLRegression method, 
			final NeuralDataSet data) {
		final ErrorCalculation errorCalculation = new ErrorCalculation();
		
		// clear context
		if( method instanceof MLContext ) {
			((MLContext)method).clearContext();
		}

		// calculate error
		for (final NeuralDataPair pair : data) {
			final NeuralData actual = method.compute(pair.getInput());
			errorCalculation.updateError(actual.getData(), pair.getIdeal()
					.getData());
		}
		return errorCalculation.calculate();
	}
}
