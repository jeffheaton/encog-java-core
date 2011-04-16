package org.encog.ml.factory.train;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.MLTrain;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.rbf.RBFNetwork;
import org.encog.neural.rbf.training.SVDTraining;

public class RBFSVDFactory {

	public MLTrain create(MLMethod method, NeuralDataSet training, String args) {
		
		if( !(method instanceof RBFNetwork) ) {
			throw new EncogError("RBF-SVD training cannot be used on a method of type: " + method.getClass().getName() );
		}
		
		return new SVDTraining((RBFNetwork) method, training);
	}
	
}
