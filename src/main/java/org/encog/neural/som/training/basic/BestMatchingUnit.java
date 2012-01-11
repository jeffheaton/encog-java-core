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
package org.encog.neural.som.training.basic;

import org.encog.mathutil.BoundMath;
import org.encog.mathutil.matrices.Matrix;
import org.encog.ml.data.MLData;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.som.SOM;

/**
 * The "Best Matching Unit" or BMU is a very important concept in the training
 * for a SOM. The BMU is the output neuron that has weight connections to the
 * input neurons that most closely match the current input vector. This neuron
 * (and its "neighborhood") are the neurons that will receive training.
 * 
 * This class also tracks the worst distance (of all BMU's). This gives some
 * indication of how well the network is trained, and thus becomes the "error"
 * of the entire network.
 * 
 * @author jeff
 * 
 */
public class BestMatchingUnit {

	/**
	 * The owner of this class.
	 */
	private final SOM som;

	/**
	 * What is the worst BMU distance so far, this becomes the error for the
	 * entire SOM.
	 */
	private double worstDistance;

	/**
	 * Construct a BestMatchingUnit class.  The training class must be provided.
	 * @param som The SOM to evaluate.
	 */
	public BestMatchingUnit(final SOM som) {
		this.som = som;
	}

	/**
	 * Calculate the best matching unit (BMU). This is the output neuron that
	 * has the lowest Euclidean distance to the input vector.
	 * 
	 * @param input
	 *            The input vector.
	 * @return The output neuron number that is the BMU.
	 */
	public int calculateBMU(final MLData input) {
		int result = 0;
		
		if( input.size()>this.som.getInputCount() ) {
			throw new NeuralNetworkError("Can't train SOM with input size of " + som.getInputCount() 
					+ " with input data of count " 
					+ input.size());
		}
		
		// Track the lowest distance so far.
		double lowestDistance = Double.MAX_VALUE;

		for (int i = 0; i < this.som.getOutputCount(); i++) {
			final double distance = calculateEuclideanDistance(this.som.getWeights(), input,
					i);

			// Track the lowest distance, this is the BMU.
			if (distance < lowestDistance) {
				lowestDistance = distance;
				result = i;
			}
		}

		// Track the worst distance, this is the error for the entire network.
		if (lowestDistance > this.worstDistance) {
			this.worstDistance = lowestDistance;
		}

		return result;
	}

	/**
	 * Calculate the Euclidean distance for the specified output neuron and the
	 * input vector.  This is the square root of the squares of the differences
	 * between the weight and input vectors.
	 * 
	 * @param matrix
	 *            The matrix to get the weights from.
	 * @param input
	 *            The input vector.
	 * @param outputNeuron
	 *            The neuron we are calculating the distance for.
	 * @return The Euclidean distance.
	 */
	public double calculateEuclideanDistance(final Matrix matrix,
			final MLData input, final int outputNeuron) {
		double result = 0;
		
		// Loop over all input data.
		for (int i = 0; i < input.size(); i++) {
			final double diff = input.getData(i)
					- matrix.get(outputNeuron, i);
			result += diff * diff;
		}
		return BoundMath.sqrt(result);
	}

	/**
	 * @return What is the worst BMU distance so far, this becomes the error 
	 * for the entire SOM.
	 */
	public double getWorstDistance() {
		return this.worstDistance;
	}

	/**
	 * Reset the "worst distance" back to a minimum value.  This should be
	 * called for each training iteration.
	 */
	public void reset() {
		this.worstDistance = Double.MIN_VALUE;
	}
}
