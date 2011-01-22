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
package org.encog.neural.thermal;

import org.encog.mathutil.matrices.Matrix;
import org.encog.mathutil.matrices.MatrixMath;
import org.encog.neural.data.NeuralData;
import org.encog.persist.map.PersistConst;
import org.encog.persist.map.PersistedObject;

public class HopfieldNetwork extends ThermalNetwork {

	public HopfieldNetwork(int neuronCount) {
		super(neuronCount);
	}
	
	public HopfieldNetwork()
	{
		
	}
	
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
				addWeight(row, col,
						delta.get(row, col));
			}
		}
	}

	/**
	 * Perform one Hopfield iteration.
	 */
	public void run() {
		
		for(int toNeuron = 0; toNeuron<getNeuronCount() ; toNeuron++ )
		{
			double sum = 0;
			for(int fromNeuron = 0; fromNeuron<getNeuronCount() ; fromNeuron++)
			{
				sum += getCurrentState().getData(fromNeuron) * getWeight(fromNeuron,toNeuron);
			}
			getCurrentState().setData(toNeuron,sum);
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
	
	public boolean supportsMapPersistence()
	{
		return true;
	}
	
	public void persistToMap(PersistedObject obj)
	{
		obj.clear(PersistConst.TYPE_HOPFIELD);
		obj.setStandardProperties(this);
		obj.setProperty(PersistConst.WEIGHTS, this.getWeights());
		obj.setProperty(PersistConst.OUTPUT, this.getCurrentState().getData());
		obj.setProperty(PersistConst.NEURON_COUNT, this.getNeuronCount(),false);
	}
	
	public void persistFromMap(PersistedObject obj)
	{
		obj.requireType(PersistConst.TYPE_HOPFIELD);
		int neuronCount = obj.getPropertyInt(PersistConst.NEURON_COUNT,true);
		double[] weights = obj.getPropertyDoubleArray(PersistConst.WEIGHTS,true);
		double[] state = obj.getPropertyDoubleArray(PersistConst.OUTPUT, true);
		init(neuronCount,weights,state);
	}
}
