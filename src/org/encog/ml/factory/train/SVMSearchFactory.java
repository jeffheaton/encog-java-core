package org.encog.ml.factory.train;

import java.util.Map;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.MLTrain;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.ml.svm.SVM;
import org.encog.ml.svm.training.SVMSearchTrain;
import org.encog.neural.data.NeuralDataSet;
import org.encog.util.ParamsHolder;

public class SVMSearchFactory {
	public MLTrain create(MLMethod method, NeuralDataSet training, String argsStr) {
		
		if( !(method instanceof SVM) ) {
			throw new EncogError("SVM Train training cannot be used on a method of type: " + method.getClass().getName() );
		}
		
		SVMSearchTrain result = new SVMSearchTrain((SVM)method,training);
		
		Map<String, String> args = ArchitectureParse.parseParams(argsStr);
		ParamsHolder holder = new ParamsHolder(args);
		
		//double gamma = holder.getDouble(MLTrainFactory.PROPERTY_GAMMA, false, defaultGamma);
		
		return result;
	}
}
