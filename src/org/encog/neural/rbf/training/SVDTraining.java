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
package org.encog.neural.rbf.training;

import org.encog.engine.network.rbf.RadialBasisFunction;
import org.encog.engine.util.ObjectPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.rbf.RBFNetwork;
import org.encog.util.simple.TrainingSetUtil;

/**
 * Train a RBF neural network using a SVD.
 * 
 * Contributed to Encog By M.Fletcher and M.Dean University of Cambridge, Dept.
 * of Physics, UK
 *
 */
public class SVDTraining extends BasicTraining {

	/**
	 * The network that is to be trained.
	 */
	private RBFNetwork network;

	/**
	 * Construct the training object.
	 * @param network The network to train. Must have a single output
	 * neuron.
	 * @param training The training data to use. Must be
	 * indexable.
	 */
	public SVDTraining(RBFNetwork network, NeuralDataSet training) {

		if (network.getOutputCount() != 1) {
			throw new TrainingError(
					"SVD requires an output layer with a single neuron.");
		}

		this.setTraining(training);
		this.network = network;
	}

	/**
	 * @return The trained network.
	 */
	public RBFNetwork getNetwork() {
		return this.network;
	}

	/**
	 * Perform one iteration.
	 */
	public void iteration() {
		int length = network.getRBF().length;
		
		RadialBasisFunction[] funcs = new RadialBasisFunction[length];

		// Iteration over neurons and determine the necessaries
		for (int i = 0; i < length; i++) {
			RadialBasisFunction basisFunc = network.getRBF()[i];

			funcs[i] = basisFunc;

			// This is the value that is changed using other training methods.
			// weights[i] =
			// network.Structure.Synapses[0].WeightMatrix.Data[i][j];
		}

		ObjectPair<double[][], double[][]> data = TrainingSetUtil
				.trainingToArray(getTraining());

		
		double[][] matrix = new double[length][network.getOutputCount()];

		flatToMatrix( this.network.getFlatRBF().getWeights(), 0, matrix );
		setError(SVD.svdfit(data.getA(), data.getB(), matrix, funcs));
		matrixToFlat( matrix, this.network.getFlatRBF().getWeights(), 0);
	}
	
	public void flatToMatrix(double[] flat, int start, double[][] matrix)
	{
		int rows = matrix.length;
		int cols = matrix[0].length;
		
		int index = start;
		
		for(int r = 0; r<rows; r++)
		{
			for(int c = 0; c< cols; c++)
			{
				matrix[r][c] = flat[index++];
			}
		}
	}
	
	public void matrixToFlat(double[][] matrix, double[] flat, int start)
	{
		int rows = matrix.length;
		int cols = matrix[0].length;
		
		int index = start;
		
		for(int r = 0; r<rows; r++)
		{
			for(int c = 0; c< cols; c++)
			{
				flat[index++] = matrix[r][c];
			}
		}
	}
}
