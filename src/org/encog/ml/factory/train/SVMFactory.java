package org.encog.ml.factory.train;

import java.util.Map;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.MLTrain;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.ml.svm.SVM;
import org.encog.ml.svm.training.SVMTrain;
import org.encog.neural.data.NeuralDataSet;
import org.encog.util.ParamsHolder;

public class SVMFactory {
	public MLTrain create(MLMethod method, NeuralDataSet training, String argsStr) {
		
		if( !(method instanceof SVM) ) {
			throw new EncogError("SVM Train training cannot be used on a method of type: " + method.getClass().getName() );
		}
		
		double defaultGamma = 1.0 / ((SVM)method).getInputCount();
		double defaultC = 1.0;
		
		Map<String, String> args = ArchitectureParse.parseParams(argsStr);
		ParamsHolder holder = new ParamsHolder(args);
		double gamma = holder.getDouble(MLTrainFactory.PROPERTY_GAMMA, false, defaultGamma);
		double c = holder.getDouble(MLTrainFactory.PROPERTY_C, false, defaultC);
		
		
		SVMTrain result = new SVMTrain((SVM) method, training);
		result.setGamma(gamma);
		result.setC(c);
		return result;
	}
}
