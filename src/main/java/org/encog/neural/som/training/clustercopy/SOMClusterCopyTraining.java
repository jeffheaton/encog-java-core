/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.neural.som.training.clustercopy;

import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.som.SOM;

public class SOMClusterCopyTraining extends BasicTraining {

	private SOM network;
	
	public SOMClusterCopyTraining(SOM network, MLDataSet training) {
		super(TrainingImplementationType.OnePass);
		this.network = network;
		setTraining(training);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MLMethod getMethod() {
		return this.network;
	}
	
	/**
	 * Copy the specified input pattern to the weight matrix. This causes an
	 * output neuron to learn this pattern "exactly". This is useful when a
	 * winner is to be forced.
	 *
	 * @param synapse
	 *            The synapse that is the target of the copy.
	 * @param outputNeuron
	 *            The output neuron to set.
	 * @param input
	 *            The input pattern to copy.
	 */
	private void copyInputPattern(final int outputNeuron, final MLData input) {
		for (int inputNeuron = 0; inputNeuron < this.network.getInputCount();
			inputNeuron++) {
			this.network.getWeights().set(inputNeuron, outputNeuron,
					input.getData(inputNeuron));
		}
	}

	@Override
	public void iteration() {
		int outputNeuron = 0;
		for(MLDataPair pair: this.getTraining() ) {
			copyInputPattern(outputNeuron++,pair.getInput());
		}
	}

	@Override
	public boolean canContinue() {
		return false;
	}

	@Override
	public TrainingContinuation pause() {
		return null;
	}

	@Override
	public void resume(TrainingContinuation state) {
	}

}
