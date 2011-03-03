package org.encog.ml.factory.train;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.MLTrain;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.lma.LevenbergMarquardtTraining;

public class LMAFactory {
	public MLTrain create(MLMethod method, NeuralDataSet training, String args) {
		
		if( !(method instanceof BasicNetwork) ) {
			throw new EncogError("LMA training cannot be used on a method of type: " + method.getClass().getName() );
		}
		
		return new LevenbergMarquardtTraining((BasicNetwork)method, training);
	}
}
