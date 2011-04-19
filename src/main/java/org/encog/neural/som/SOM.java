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
import org.encog.ml.data.basic.BasicMLDataArray;
import org.encog.neural.som.training.basic.BestMatchingUnit;
import org.encog.util.EngineArray;

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
	Matrix weights;

	/**
	 * Number of input neurons
	 */
	protected int inputNeuronCount;

	/**
	 * Number of output neurons
	 */
	protected int outputNeuronCount;

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

		this.inputNeuronCount = inputCount;
		this.outputNeuronCount = outputCount;
		this.weights = new Matrix(inputCount, outputCount);
	}

	/**
	 * Get the input neuron count.
	 * @return The input neuron count.
	 */
	public int getInputNeuronCount() {
		return this.inputNeuronCount;
	}

	/**
	 * Get the output neuron count.
	 * @return The output neuron count.
	 */
	public int getOutputNeuronCount() {
		return this.outputNeuronCount;
	}

	/**
	 * Determine the winner for the specified input. This is the number of the
	 * winning neuron.
	 * @param input The input pattern.
	 * @return The winning neuron.
	 */
	public int winner(final MLData input) {

		MLData output = compute(input);
		int win = EngineArray.indexOfLargest(output.getData());
		return win;
	}

	/**
	 * Determine the winner for the specified input. This is the number of the
	 * winning neuron.
	 * @param input The input pattern.
	 * @return The winning neuron.
	 */
	public MLData compute(final MLData input) {

		MLData result = new BasicMLDataArray(this.outputNeuronCount);

		for (int i = 0; i < this.outputNeuronCount; i++) {
			final Matrix optr = this.weights.getCol(i);
			final Matrix inputMatrix = Matrix.createRowMatrix(input.getData());
			result.setData(i, MatrixMath.dotProduct(inputMatrix, optr));
		}

		return result;
	}

	/**
	 * @return the weights
	 */
	public Matrix getWeights() {
		return weights;
	}

	/**
	 * @param weights the weights to set
	 */
	public void setWeights(Matrix weights) {
		this.weights = weights;
	}

	public boolean supportsMapPersistence() {
		return true;
	}


	public void reset() {
		this.weights.randomize(-1, 1);

	}

	@Override
	public void updateProperties() {
		// unneeded
	}

	@Override
	public int classify(MLData input) {
		MLData result = this.compute(input);
		return EngineArray.maxIndex(result.getData());
	}

	@Override
	public int getInputCount() {
		return this.inputNeuronCount;
	}

	@Override
	public int getOutputCount() {
		return 1;
	}

	@Override
	public void reset(int seed) {
		reset();
	}

	@Override
	public double calculateError(MLDataSet data) {

		BestMatchingUnit bmu = new BestMatchingUnit(this);

		bmu.reset();

		// Determine the BMU for each training element.
		for (final MLDataPair pair : data) {
			final MLData input = pair.getInput();
			bmu.calculateBMU(input);
		}

		// update the error
		return bmu.getWorstDistance()/100.0;
	}

	
	public void setOutputNeuronCount(int i) {
		this.outputNeuronCount = i;
		
	}

	public void setInputCount(int i) {
		this.inputNeuronCount = i;		
	}

}
