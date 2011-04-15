package org.encog.ml.factory.train;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.MLTrain;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.som.SOM;
import org.encog.neural.som.training.clustercopy.SOMClusterCopyTraining;

public class ClusterSOMFactory {
	public MLTrain create(MLMethod method, NeuralDataSet training, String argsStr) {
		
		if( !(method instanceof SOM) ) {
			throw new EncogError("Cluster SOM training cannot be used on a method of type: " + method.getClass().getName() );
		}
		
		return new SOMClusterCopyTraining((SOM)method, training);
	}
}
