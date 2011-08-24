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
package org.encog.mathutil.matrices.hessian;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.error.ErrorCalculation;
import org.encog.mathutil.matrices.Matrix;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.EngineArray;

/**
 * Calculate the Hessian matrix using the chain rule method. 
 * 
 */
public class HessianCR extends BasicHessian {
	
	
	/**
	 * The actual values from the neural network.
	 */
	private double[] actual;

	/**
	 * The deltas for each layer.
	 */
	private double[] layerDelta;

	/**
	 * The neuron counts, per layer.
	 */
	private int[] layerCounts;

	/**
	 * The feed counts, per layer.
	 */
	private int[] layerFeedCounts;

	/**
	 * The layer indexes.
	 */
	private int[] layerIndex;

	/**
	 * The index to each layer's weights and thresholds.
	 */
	private int[] weightIndex;

	/**
	 * The output from each layer.
	 */
	private double[] layerOutput;
	
	/**
	 * The sums.
	 */
	private double[] layerSums;

	
	/**
	 * The weights and thresholds.
	 */
	private double[] weights;	
	
	/**
	 * {@inheritDoc}
	 */
	public void init(BasicNetwork theNetwork, MLDataSet theTraining) {
		
		super.init(theNetwork,theTraining);
		int weightCount = theNetwork.getStructure().getFlat().getWeights().length;
		
		this.training = theTraining;
		this.network = theNetwork;
		
		this.layerDelta = new double[flat.getLayerOutput().length];	
		this.actual = new double[flat.getOutputCount()];
		this.hessianMatrix = new Matrix(weightCount,weightCount);
		this.hessian = this.hessianMatrix.getData();

		this.weights = flat.getWeights();
		this.layerIndex = flat.getLayerIndex();
		this.layerCounts = flat.getLayerCounts();
		this.weightIndex = flat.getWeightIndex();
		this.layerOutput = flat.getLayerOutput();
		this.layerSums = flat.getLayerSums();
		this.layerFeedCounts = flat.getLayerFeedCounts();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void compute() {
		double e;
		int weightCount = this.network.getFlat().getWeights().length;
		
		ErrorCalculation error = new ErrorCalculation();
		double[] totDeriv = new double[weightCount];

		for (int outputNeuron = 0; outputNeuron < this.network.getOutputCount(); outputNeuron++) {
			EngineArray.fill(totDeriv, 0);
			
			// Loop over every training element
			for (final MLDataPair pair : this.training) {
				final MLData networkOutput = this.network.compute(pair
						.getInput());

				EngineArray.fill(this.derivative, 0);

				e = pair.getIdeal().getData(outputNeuron) - networkOutput.getData(outputNeuron);
				error.updateError(networkOutput.getData(outputNeuron), pair.getIdeal()
						.getData(outputNeuron));

				process(outputNeuron, pair.getInputArray(), pair.getIdealArray());

				// calculate gradients
				for (int i = 0; i < this.weights.length; i++) {
					this.gradients[i] += e * this.derivative[i];
					totDeriv[i] += this.derivative[i];
				}
			}
			updateHessian(totDeriv);
		}
		
		

		sse= error.calculateESS();
	}
	
	/**
	 * Process one training set element.
	 * 
	 * @param input
	 *            The network input.
	 * @param ideal
	 *            The ideal values.      
	 */
	private void process(int outputNeuron, final double[] input, final double[] ideal) {
				
		this.network.compute(input, this.actual);

		for (int i = 0; i < this.actual.length; i++) {

			if (i == outputNeuron) {
				this.layerDelta[i] = this.flat.getActivationFunctions()[0]
						.derivativeFunction(this.layerSums[i],
								this.layerOutput[i]);
			} else {
				this.layerDelta[i] = 0;
			}
		}

		for (int i = this.flat.getBeginTraining(); i < this.flat.getEndTraining(); i++) {
			processLevel(i);
		}
	}

	/**
	 * Process one level.
	 * 
	 * @param currentLevel
	 *            The level.
	 */
	private void processLevel(final int currentLevel) {
		final int fromLayerIndex = this.layerIndex[currentLevel + 1];
		final int toLayerIndex = this.layerIndex[currentLevel];
		final int fromLayerSize = this.layerCounts[currentLevel + 1];
		final int toLayerSize = this.layerFeedCounts[currentLevel];

		final int index = this.weightIndex[currentLevel];
		final ActivationFunction activation = this.flat
				.getActivationFunctions()[currentLevel + 1];

		// handle weights
		int yi = fromLayerIndex;
		for (int y = 0; y < fromLayerSize; y++) {
			final double output = this.layerOutput[yi];
			double sum = 0;
			int xi = toLayerIndex;
			int wi = index + y;
			for (int x = 0; x < toLayerSize; x++) {
				this.derivative[wi] += output * this.layerDelta[xi];
				sum += this.weights[wi] * this.layerDelta[xi];
				wi += fromLayerSize;
				xi++;
			}

			this.layerDelta[yi] = sum
					* (activation.derivativeFunction(this.layerSums[yi],this.layerOutput[yi]));
			yi++;
		}
	}
}
