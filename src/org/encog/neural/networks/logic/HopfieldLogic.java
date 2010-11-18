/*
 * Encog(tm) Core v2.6 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
package org.encog.neural.networks.logic;

import org.encog.mathutil.matrices.Matrix;
import org.encog.mathutil.matrices.MatrixMath;
import org.encog.neural.data.NeuralData;

/**
 * Provides the neural logic for an Hopfield type network. See HopfieldPattern
 * for more information on this type of network.
 */
public class HopfieldLogic extends ThermalLogic {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 6522005686471473074L;

	/**
	 * Train the neural network for the specified pattern. The neural network
	 * can be trained for more than one pattern. To do this simply call the
	 * train method more than once.
	 * 
	 * @param pattern
	 *            The pattern to train for.
	 */
	public void addPattern(final NeuralData pattern) {

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
	 * Update the Hopfield weights after training.
	 * 
	 * @param delta
	 *            The amount to change the weights by.
	 */
	private void convertHopfieldMatrix(final Matrix delta) {
		// add the new weight matrix to what is there already
		for (int row = 0; row < delta.getRows(); row++) {
			for (int col = 0; col < delta.getRows(); col++) {
				getThermalSynapse().getMatrix().add(row, col,
						delta.get(row, col));
			}
		}
	}

	/**
	 * Perform one Hopfield iteration.
	 */
	public void run() {
		final NeuralData temp = this.compute(getCurrentState(), null);
		for (int i = 0; i < temp.size(); i++) {
			getCurrentState().setData(i, temp.getData(i) > 0);
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
	public int runUntilStable(final int max) {
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
}
