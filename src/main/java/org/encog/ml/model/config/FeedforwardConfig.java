/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.model.config;

import org.encog.EncogError;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.normalizers.strategies.BasicNormalizationStrategy;
import org.encog.ml.data.versatile.normalizers.strategies.NormalizationStrategy;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.neural.networks.BasicNetwork;

/**
 * Config class for EncogModel to use a feedforward neural network.
 */
public class FeedforwardConfig implements MethodConfig {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMethodName() {
		return MLMethodFactory.TYPE_FEEDFORWARD;
	}
	
	/**
	 * {@inheritDoc}
	 */
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
	
	/**
	 * {@inheritDoc}
	 */
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
		
		// check output function
		ActivationFunction outputFunction = network.getActivation(network.getLayerCount()-1);
		
		double[] d = { -1000, -100, -50 };
		outputFunction.activationFunction(d, 0, d.length);
		
		if( d[0]>0 && d[1]>0 && d[2]>0 ) {
			outputLow=0;
		}
		
		// check input function
		ActivationFunction inputFunction = network.getActivation(1);
		
		double[] d2 = { -1000, -100, -50 };
		inputFunction.activationFunction(d2, 0, d2.length);
		
		if( d2[0]>0 && d2[1]>0 && d2[2]>0 ) {
			inputLow=0;
		}
		
		NormalizationStrategy result = new BasicNormalizationStrategy(
				inputLow,
				inputHigh,
				outputLow,
				outputHigh);
		return result;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String suggestTrainingType() {
		return "rprop";
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String suggestTrainingArgs(String trainingType) {
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int determineOutputCount(VersatileMLDataSet dataset) {
		return dataset.getNormHelper().calculateNormalizedOutputCount();
	}
}
