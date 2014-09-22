package org.encog.ml.model.config;

import org.encog.EncogError;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.normalizers.strategies.BasicNormalizationStrategy;
import org.encog.ml.data.versatile.normalizers.strategies.NormalizationStrategy;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.neural.networks.BasicNetwork;

public class FeedforwardConfig implements MethodConfig {
	
	@Override
	public String getMethodName() {
		return MLMethodFactory.TYPE_FEEDFORWARD;
	}
	
	@Override
	public String suggestModelArchitecture(VersatileMLDataSet dataset) {
		int inputColumns = dataset.getNormHelper().getInputColumns().size();
		int outputColumns = dataset.getNormHelper().getOutputColumns().size();
		int hiddenCount = (int) ((double)(inputColumns+outputColumns) * 1.5);
		StringBuilder result = new StringBuilder();
		result.append("?:B->TANH->");
		result.append(hiddenCount);
		result.append(":B->TANH->?");
		return result.toString();
	}
	
	@Override
	public NormalizationStrategy suggestNormalizationStrategy(VersatileMLDataSet dataset, String architecture) {
		double inputLow = -1;
		double inputHigh = 1;
		double outputLow = -1;
		double outputHigh = 1;
		
		// Create a basic neural network, just to examine activation functions.
		MLMethodFactory methodFactory = new MLMethodFactory();		
		BasicNetwork network = (BasicNetwork)methodFactory.create(getMethodName(), architecture, 1, 1);
		
		if( network.getLayerCount()<1 ) {
			throw new EncogError("Neural network does not have an output layer.");
		}
		
		ActivationFunction outputFunction = network.getActivation(network.getLayerCount()-1);
		
		double[] d = { -1000, -100, -50 };
		outputFunction.activationFunction(d, 0, d.length);
		
		if( d[0]>0 && d[1]>0 && d[2]>0 ) {
			inputLow=0;
		}
		
		NormalizationStrategy result = new BasicNormalizationStrategy(
				inputLow,
				inputHigh,
				outputLow,
				outputHigh);
		return result;
	}


	@Override
	public String suggestTrainingType() {
		return "rprop";
	}


	@Override
	public String suggestTrainingArgs(String trainingType) {
		return "";
	}

	@Override
	public int determineOutputCount(VersatileMLDataSet dataset) {
		return dataset.getNormHelper().calculateNormalizedOutputCount();
	}
}
