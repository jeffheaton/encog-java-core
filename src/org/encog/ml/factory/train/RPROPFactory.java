package org.encog.ml.factory.train;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.MLTrain;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

public class RPROPFactory {
	public MLTrain create(MLMethod method, NeuralDataSet training, String args) {
		
		if( !(method instanceof BasicNetwork) ) {
			throw new EncogError("RPROP training cannot be used on a method of type: " + method.getClass().getName() );
		}
		
		return new ResilientPropagation((BasicNetwork) method, training);
	}
}
