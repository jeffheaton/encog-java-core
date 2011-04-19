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
package org.encog.neural.bam;

import org.encog.mathutil.matrices.Matrix;
import org.encog.ml.BasicML;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.MLDataArray;
import org.encog.neural.networks.NeuralDataMapping;

public class BAM extends BasicML {
	
	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

	private int f1Count;
	private int f2Count;
	
	private Matrix weightsF1toF2;
	private Matrix weightsF2toF1;

	public BAM()
	{
	
	}
	
	public BAM(int f1Count,int f2Count)
	{
		this.f1Count = f1Count;
		this.f2Count = f2Count;
		
		this.weightsF1toF2 = new Matrix(f1Count,f2Count);
		this.weightsF2toF1 = new Matrix(f2Count,f1Count);		
	}



	/**
	 * Add a pattern to the neural network.
	 * 
	 * @param inputPattern
	 *            The input pattern.
	 * @param outputPattern
	 *            The output pattern(for this input).
	 */
	public void addPattern(final MLDataArray inputPattern,
			final MLDataArray outputPattern) {

		int weight;

		for (int i = 0; i < this.f1Count; i++) {
			for (int j = 0; j < this.f2Count; j++) {
				weight = (int) (inputPattern.getData(i) * outputPattern
						.getData(j));
				weightsF1toF2.add(i, j, weight);
				weightsF2toF1.add(j, i, weight);
			}
		}

	}

	/**
	 * Clear any connection weights.
	 */
	public void clear() {
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
	public MLDataArray compute(final MLDataArray input) {
		throw new NeuralNetworkError("Compute on BasicNetwork cannot be used, rather call"
				+ " the compute(NeuralData) method on the BAMLogic.");

	}

	/**
	 * Compute the network for the specified input.
	 * 
	 * @param input
	 *            The input to the network.
	 * @return The output from the network.
	 */
	public NeuralDataMapping compute(final NeuralDataMapping input) {

		boolean stable1 = true, stable2 = true;

		do {

			stable1 = propagateLayer(weightsF1toF2, input.getFrom(), input
					.getTo());
			stable2 = propagateLayer(weightsF2toF1, input.getTo(), input
					.getFrom());

		} while (!stable1 && !stable2);
		return null;
	}

	/**
	 * Get the specified weight.
	 * 
	 * @param synapse
	 *            The synapse to get the weight from.
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
	private double getWeight(final Matrix matrix, final MLDataArray input,
			final int x, final int y) {
		if (matrix.getRows() != input.size()) {
			return matrix.get(x, y);
		} else {
			return matrix.get(y, x);
		}
	}


	/**
	 * Propagate the layer.
	 * 
	 * @param synapse
	 *            The synapse for this layer.
	 * @param input
	 *            The input pattern.
	 * @param output
	 *            The output pattern.
	 * @return True if the network has become stable.
	 */
	private boolean propagateLayer(final Matrix matrix,
			final MLDataArray input, final MLDataArray output) {
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
	 * @return the f1Count
	 */
	public int getF1Count() {
		return f1Count;
	}

	/**
	 * @return the f2Count
	 */
	public int getF2Count() {
		return f2Count;
	}

	/**
	 * @return the weightsF1toF2
	 */
	public Matrix getWeightsF1toF2() {
		return weightsF1toF2;
	}

	/**
	 * @return the weightsF2toF1
	 */
	public Matrix getWeightsF2toF1() {
		return weightsF2toF1;
	}
	

	@Override
	public void updateProperties() {
		// TODO Auto-generated method stub
		
	}

	public void setF1Count(int i) {
		this.f1Count = i;		
	}
	
	public void setF2Count(int i) {
		this.f2Count = i;		
	}

	public void setWeightsF1toF2(Matrix matrix) {
		this.weightsF1toF2 = matrix;		
	}

	public void setWeightsF2toF1(Matrix matrix) {
		this.weightsF2toF1 = matrix;		
	}

}
