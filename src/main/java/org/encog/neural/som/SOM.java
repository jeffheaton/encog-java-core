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
package org.encog.neural.som;

import org.encog.mathutil.matrices.Matrix;
import org.encog.mathutil.matrices.MatrixMath;
import org.encog.ml.BasicML;
import org.encog.ml.MLClassification;
import org.encog.ml.MLError;
import org.encog.ml.MLResettable;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.som.training.basic.BestMatchingUnit;
import org.encog.util.EngineArray;

/**
 * A self organizing map neural network.
 *
 */
public class SOM extends BasicML implements MLClassification, MLResettable,
		MLError {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Do not allow patterns to go below this very small number.
	 */
	public static final double VERYSMALL = 1.E-30;

	/**
	 * The weights of the output neurons base on the input from the input
	 * neurons.
	 */
	private Matrix weights;


	/**
	 * Default constructor.
	 */
	public SOM() {

	}

	/**
	 * The constructor.
	 * 
	 * @param inputCount
	 *            Number of input neurons
	 * @param outputCount
	 *            Number of output neurons
	 */
	public SOM(final int inputCount, final int outputCount) {
		this.weights = new Matrix(outputCount, inputCount);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double calculateError(final MLDataSet data) {

		final BestMatchingUnit bmu = new BestMatchingUnit(this);

		bmu.reset();

		// Determine the BMU for each training element.
		for (final MLDataPair pair : data) {
			final MLData input = pair.getInput();
			bmu.calculateBMU(input);
		}

		// update the error
		return bmu.getWorstDistance() / 100.0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int classify(final MLData input) {
		if( input.size()>getInputCount() ) {
			throw new NeuralNetworkError("Can't classify SOM with input size of " + getInputCount() 
					+ " with input data of count " 
					+ input.size());
		}
		
		double[][] m = this.weights.getData();
		double[] inputData = input.getData();
		double minDist = Double.POSITIVE_INFINITY;
		int result = -1;
		
		for(int i=0;i<getOutputCount();i++) {
			double dist = EngineArray.euclideanDistance(inputData, m[i]);
			if( dist<minDist ) {
				minDist = dist;
				result = i;
			}
		}
		
		return result;
	}

	/**
	 * Calculate the output of the SOM, for each output neuron.  Typically,
	 * you will use the classify method instead of calling this method.
	 * @param input
	 *            The input pattern.
	 * @return The output activation of each output neuron.
	 */
	public final MLData compute(final MLData input) {
		
		if( input.size()>getInputCount() ) {
			throw new NeuralNetworkError("Can't compute SOM with input size of " + getInputCount() 
					+ " with input data of count " 
					+ input.size());
		}


		final MLData result = new BasicMLData(this.getOutputCount());

		for (int i = 0; i < this.getOutputCount(); i++) {
			final Matrix optr = this.weights.getRow(i);
			final Matrix inputMatrix = Matrix.createRowMatrix(input.getData());
			result.setData(i, MatrixMath.dotProduct(inputMatrix, optr));
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getInputCount() {
		return this.weights.getCols();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getOutputCount() {
		return this.weights.getRows();
	}

	/**
	 * @return the weights
	 */
	public final Matrix getWeights() {
		return this.weights;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void reset() {
		this.weights.randomize(-1, 1);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void reset(final int seed) {
		reset();
	}

	/**
	 * @param weights
	 *            the weights to set
	 */
	public final void setWeights(final Matrix weights) {
		this.weights = weights;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void updateProperties() {
		// unneeded
	}

	/**
	 * Determine the winner for the specified input. This is the number of the
	 * winning neuron.
	 * 
	 * @param input
	 *            The input pattern.
	 * @return The winning neuron.
	 */
	public final int winner(final MLData input) {

		final MLData output = compute(input);
		final int win = EngineArray.indexOfLargest(output.getData());
		return win;
	}

}
