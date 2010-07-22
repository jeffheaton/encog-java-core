/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.neural.networks.training.competitive;

import org.encog.engine.util.BoundMath;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.synapse.Synapse;

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
	private final CompetitiveTraining training;

	/**
	 * What is the worst BMU distance so far, this becomes the error for the
	 * entire SOM.
	 */
	private double worstDistance;

	/**
	 * Construct a BestMatchingUnit class.  The training class must be provided.
	 * @param training The parent class.
	 */
	public BestMatchingUnit(final CompetitiveTraining training) {
		this.training = training;
	}

	/**
	 * Calculate the best matching unit (BMU). This is the output neuron that
	 * has the lowest Euclidean distance to the input vector.
	 * 
	 * @param synapse
	 *            The synapse to calculate for.
	 * @param input
	 *            The input vector.
	 * @return The output neuron number that is the BMU.
	 */
	public int calculateBMU(final Synapse synapse, final NeuralData input) {
		int result = 0;
		
		// Track the lowest distance so far.
		double lowestDistance = Double.MAX_VALUE;

		for (int i = 0; i < this.training.getOutputNeuronCount(); i++) {
			final double distance = calculateEuclideanDistance(synapse, input,
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
	 * @param synapse
	 *            The synapse to get the weights from.
	 * @param input
	 *            The input vector.
	 * @param outputNeuron
	 *            The neuron we are calculating the distance for.
	 * @return The Euclidean distance.
	 */
	public double calculateEuclideanDistance(final Synapse synapse,
			final NeuralData input, final int outputNeuron) {
		double result = 0;
		
		// Loop over all input data.
		for (int i = 0; i < input.size(); i++) {
			final double diff = input.getData(i)
					- synapse.getMatrix().get(i, outputNeuron);
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
