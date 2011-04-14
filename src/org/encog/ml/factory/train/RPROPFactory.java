package org.encog.ml.factory.train;

import java.util.Map;

import org.encog.EncogError;
import org.encog.engine.network.train.prop.RPROPConst;
import org.encog.ml.MLMethod;
import org.encog.ml.MLTrain;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.ParamsHolder;

public class RPROPFactory {
	public MLTrain create(MLMethod method, NeuralDataSet training, String argsStr) {
		
		if( !(method instanceof BasicNetwork) ) {
			throw new EncogError("RPROP training cannot be used on a method of type: " + method.getClass().getName() );
		}
		
		Map<String, String> args = ArchitectureParse.parseParams(argsStr);
		ParamsHolder holder = new ParamsHolder(args);
		double initialUpdate = holder.getDouble(MLTrainFactory.PROPERTY_INITIAL_UPDATE, false, RPROPConst.DEFAULT_INITIAL_UPDATE);
		double maxStep = holder.getDouble(MLTrainFactory.PROPERTY_MAX_STEP, false, RPROPConst.DEFAULT_MAX_STEP);
		
		return new ResilientPropagation((BasicNetwork) method, training,initialUpdate,maxStep);
	}
}
