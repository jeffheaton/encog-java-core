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
package org.encog.neural.thermal;

import org.encog.mathutil.matrices.BiPolarUtil;
import org.encog.mathutil.matrices.Matrix;
import org.encog.mathutil.matrices.MatrixMath;
import org.encog.ml.data.MLData;
import org.encog.ml.data.specific.BiPolarNeuralData;
import org.encog.neural.NeuralNetworkError;
import org.encog.util.EngineArray;

/**
 * Implements a Hopfield network.
 * 
 */
public class HopfieldNetwork extends ThermalNetwork {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public HopfieldNetwork() {

	}

	/**
	 * Construct a Hopfield with the specified neuron count.
	 * @param neuronCount The neuron count.
	 */
	public HopfieldNetwork(final int neuronCount) {
		super(neuronCount);
	}

	/**
	 * Train the neural network for the specified pattern. The neural network
	 * can be trained for more than one pattern. To do this simply call the
	 * train method more than once.
	 * 
	 * @param pattern
	 *            The pattern to train for.
	 */
	public final void addPattern(final MLData pattern) {

		if (pattern.size() != getNeuronCount()) {
			throw new NeuralNetworkError("Network with " + getNeuronCount()
					+ " neurons, cannot learn a pattern of size "
					+ pattern.size());
		}

		// Create a row matrix from the input, convert boolean to bipolar
		final Matrix m2 = Matrix.createRowMatrix(pattern.getData());
		// Transpose the matrix and multiply by the original input matrix
		final Matrix m1 = MatrixMath.transpose(m2);
		final Matrix m3 = MatrixMath.multiply(m1, m2);

		// matrix 3 should be square by now, so create an identity
		// matrix of the same size.
		final Matrix identity = MatrixMath.identity(m3.getRows());

		// subtract the identity matrix
		final Matrix m4 = MatrixMath.subtract(m3, identity);

		// now add the calculated matrix, for this pattern, to the
		// existing weight matrix.
		convertHopfieldMatrix(m4);
	}

	/**
	 * Note: for Hopfield networks, you will usually want to call the "run"
	 * method to compute the output.
	 * 
	 * This method can be used to copy the input data to the current state. A
	 * single iteration is then run, and the new current state is returned.
	 * 
	 * @param input
	 *            The input pattern.
	 * @return The new current state.
	 */
	@Override
	public final MLData compute(final MLData input) {
		final BiPolarNeuralData result = new BiPolarNeuralData(input.size());
		EngineArray.arrayCopy(input.getData(), getCurrentState().getData());
		run();

		for (int i = 0; i < getCurrentState().size(); i++) {
			result.setData(i,
					BiPolarUtil.double2bipolar(getCurrentState().getData(i)));
		}
		EngineArray.arrayCopy(getCurrentState().getData(), result.getData());
		return result;
	}

	/**
	 * Update the Hopfield weights after training.
	 * 
	 * @param delta
	 *            The amount to change the weights by.
	 */
	private void convertHopfieldMatrix(final Matrix delta) {
		// add the new weight matrix to what is there already
		for (int row = 0; row < delta.getRows(); row++) {
			for (int col = 0; col < delta.getRows(); col++) {
				addWeight(row, col, delta.get(row, col));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getInputCount() {
		return super.getNeuronCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getOutputCount() {
		return super.getNeuronCount();
	}

	/**
	 * Perform one Hopfield iteration.
	 */
	public final void run() {

		for (int toNeuron = 0; toNeuron < getNeuronCount(); toNeuron++) {
			double sum = 0;
			for (int fromNeuron = 0; fromNeuron < getNeuronCount(); fromNeuron++) {
				sum += getCurrentState().getData(fromNeuron)
						* getWeight(fromNeuron, toNeuron);
			}
			getCurrentState().setData(toNeuron, sum);
		}
	}

	/**
	 * Run the network until it becomes stable and does not change from more
	 * runs.
	 * 
	 * @param max
	 *            The maximum number of cycles to run before giving up.
	 * @return The number of cycles that were run.
	 */
	public final int runUntilStable(final int max) {
		boolean done = false;
		String lastStateStr = getCurrentState().toString();
		String currentStateStr = getCurrentState().toString();

		int cycle = 0;
		do {
			run();
			cycle++;

			lastStateStr = getCurrentState().toString();

			if (!currentStateStr.equals(lastStateStr)) {
				if (cycle > max) {
					done = true;
				}
			} else {
				done = true;
			}

			currentStateStr = lastStateStr;

		} while (!done);

		return cycle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateProperties() {
		// nothing needed here
	}

}
