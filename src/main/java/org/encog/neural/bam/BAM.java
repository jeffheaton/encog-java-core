/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.neural.bam;

import org.encog.mathutil.matrices.Matrix;
import org.encog.ml.BasicML;
import org.encog.ml.data.MLData;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.NeuralDataMapping;

/**
 * Bidirectional associative memory (BAM) is a type of neural network 
 * developed by Bart Kosko in 1988. The BAM is a recurrent neural network 
 * that works similarly that allows patterns of different lengths to be 
 * mapped bidirectionally to other patterns. This allows it to act as 
 * almost a two-way hash map. During training the BAM is fed pattern pairs. 
 * The two halves of each pattern do not have to be the to be of the 
 * same length. However all patterns must be of the same overall structure. 
 * The BAM can be fed a distorted pattern on either side and will attempt 
 * to map to the correct value.
 * 
 * @author jheaton
 *
 */
public class BAM extends BasicML {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Neurons in the F1 layer.
	 */
	private int f1Count;
	
	/**
	 * Neurons in the F2 layer.
	 */
	private int f2Count;

	/**
	 * The weights between the F1 and F2 layers.
	 */
	private Matrix weightsF1toF2;
	
	/**
	 * The weights between the F1 and F2 layers.
	 */
	private Matrix weightsF2toF1;

	/**
	 * Default constructor, used mainly for persistence.
	 */
	public BAM() {

	}

	/**
	 * Construct the BAM network.
	 * @param theF1Count The F1 count.
	 * @param theF2Count The F2 count.
	 */
	public BAM(final int theF1Count, final int theF2Count) {
		this.f1Count = theF1Count;
		this.f2Count = theF2Count;

		this.weightsF1toF2 = new Matrix(f1Count, f2Count);
		this.weightsF2toF1 = new Matrix(f2Count, f1Count);
	}

	/**
	 * Add a pattern to the neural network.
	 * 
	 * @param inputPattern
	 *            The input pattern.
	 * @param outputPattern
	 *            The output pattern(for this input).
	 */
	public final void addPattern(final MLData inputPattern, 
			final MLData outputPattern) {

		int weight;

		for (int i = 0; i < this.f1Count; i++) {
			for (int j = 0; j < this.f2Count; j++) {
				weight = (int) (inputPattern.getData(i) * outputPattern
						.getData(j));
				this.weightsF1toF2.add(i, j, weight);
				this.weightsF2toF1.add(j, i, weight);
			}
		}

	}

	/**
	 * Clear any connection weights.
	 */
	public final void clear() {
		this.weightsF1toF2.clear();
		this.weightsF2toF1.clear();
	}

	/**
	 * Setup the network logic, read parameters from the network. NOT USED, call
	 * compute(NeuralInputData).
	 * 
	 * @param input
	 *            NOT USED
	 * @return NOT USED
	 */
	public final MLData compute(final MLData input) {
		throw new NeuralNetworkError(
				"Compute on BasicNetwork cannot be used, rather call"
						+ " the compute(NeuralData) method on the BAMLogic.");

	}

	/**
	 * Compute the network for the specified input.
	 * 
	 * @param input
	 *            The input to the network.
	 * @return The output from the network.
	 */
	public final NeuralDataMapping compute(final NeuralDataMapping input) {

		boolean stable1 = true, stable2 = true;

		do {

			stable1 = propagateLayer(this.weightsF1toF2, input.getFrom(),
					input.getTo());
			stable2 = propagateLayer(this.weightsF2toF1, input.getTo(),
					input.getFrom());

		} while (!stable1 && !stable2);
		return null;
	}

	/**
	 * @return the f1Count
	 */
	public final int getF1Count() {
		return this.f1Count;
	}

	/**
	 * @return the f2Count
	 */
	public final int getF2Count() {
		return this.f2Count;
	}

	/**
	 * Get the specified weight.
	 * 
	 * @param matrix The matrix to use.
	 * @param input
	 *            The input, to obtain the size from.
	 * @param x
	 *            The x matrix value. (could be row or column, depending on
	 *            input)
	 * @param y
	 *            The y matrix value. (could be row or column, depending on
	 *            input)
	 * @return The value from the matrix.
	 */
	private double getWeight(final Matrix matrix, final MLData input,
			final int x, final int y) {
		if (matrix.getRows() != input.size()) {
			return matrix.get(x, y);
		} else {
			return matrix.get(y, x);
		}
	}

	/**
	 * @return the weightsF1toF2
	 */
	public final Matrix getWeightsF1toF2() {
		return this.weightsF1toF2;
	}

	/**
	 * @return the weightsF2toF1
	 */
	public final Matrix getWeightsF2toF1() {
		return this.weightsF2toF1;
	}

	/**
	 * Propagate the layer.
	 * 
	 * @param matrix
	 *            The matrix for this layer.
	 * @param input
	 *            The input pattern.
	 * @param output
	 *            The output pattern.
	 * @return True if the network has become stable.
	 */
	private boolean propagateLayer(final Matrix matrix, final MLData input,
			final MLData output) {
		int i, j;
		int sum, out = 0;
		boolean stable;

		stable = true;

		for (i = 0; i < output.size(); i++) {
			sum = 0;
			for (j = 0; j < input.size(); j++) {
				sum += getWeight(matrix, input, i, j) * input.getData(j);
			}
			if (sum != 0) {
				if (sum < 0) {
					out = -1;
				} else {
					out = 1;
				}
				if (out != (int) output.getData(i)) {
					stable = false;
					output.setData(i, out);
				}
			}
		}
		return stable;
	}

	/**
	 * Set the F1 neuron count.
	 * @param i The count.
	 */
	public final void setF1Count(final int i) {
		this.f1Count = i;
	}

	/**
	 * Set the F2 neuron count.
	 * @param i The count.
	 */
	public final void setF2Count(final int i) {
		this.f2Count = i;
	}

	/**
	 * Set the weights for F1 to F2.
	 * @param matrix The weight matrix.
	 */
	public final void setWeightsF1toF2(final Matrix matrix) {
		this.weightsF1toF2 = matrix;
	}

	/**
	 * Set the weights for F2 to F1.
	 * @param matrix The weight matrix.
	 */
	public final void setWeightsF2toF1(final Matrix matrix) {
		this.weightsF2toF1 = matrix;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateProperties() {
		// TODO Auto-generated method stub
	}
}
