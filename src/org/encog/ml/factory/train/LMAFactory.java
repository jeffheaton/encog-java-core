package org.encog.ml.factory.train;

import java.util.Map;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.MLTrain;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.lma.LevenbergMarquardtTraining;
import org.encog.util.ParamsHolder;

public class LMAFactory {
	public MLTrain create(MLMethod method, NeuralDataSet training, String argsStr) {
		
		if( !(method instanceof BasicNetwork) ) {
			throw new EncogError("LMA training cannot be used on a method of type: " + method.getClass().getName() );
		}
		
		Map<String, String> args = ArchitectureParse.parseParams(argsStr);
		ParamsHolder holder = new ParamsHolder(args);
		boolean useReg = holder.getBoolean(MLTrainFactory.PROPERTY_BAYESIAN_REGULARIZATION, false, false);
		
		LevenbergMarquardtTraining result = new LevenbergMarquardtTraining((BasicNetwork)method, training);
		result.setUseBayesianRegularization(useReg);
		return result;
	}
}
