package org.encog.ml.factory.train;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.MLTrain;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.scg.ScaledConjugateGradient;

public class SCGFactory {
	public MLTrain create(MLMethod method, NeuralDataSet training, String args) {
		
		if( !(method instanceof BasicNetwork) ) {
			throw new EncogError("SCG training cannot be used on a method of type: " + method.getClass().getName() );
		}
		
		return new ScaledConjugateGradient((BasicNetwork)method, training);
	}
}
