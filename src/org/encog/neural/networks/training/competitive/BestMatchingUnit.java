package org.encog.neural.networks.training.competitive;

import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.util.math.BoundMath;

public class BestMatchingUnit {

	private final CompetitiveTraining training;
	
	/**
	 * What is the worst BMU distance so far, this becomes the error.
	 */
	private double worstDistance;
	
	private double lowestDistance;
	
	public BestMatchingUnit(CompetitiveTraining training) {
		this.training = training;
	}
	
	public void reset()
	{
		this.worstDistance = Double.MIN_VALUE;
	}
	
	/**
	 * Calculate the best matching unit (BMU). This is the output neuron that
	 * has the lowest euclidean distance to the input vector.
	 * 
	 * @param synapse
	 *            The synapse to calculate for.
	 * @param input
	 *            The input vector.
	 * @return The output neuron number that is the BMU.
	 */
	public int calculateBMU(final Synapse synapse, final NeuralData input) {
		int result = 0;
		this.lowestDistance = Double.MAX_VALUE;

		for (int i = 0; i < this.training.getOutputNeuronCount(); i++) {
			final double distance = calculateEuclideanDistance(synapse, input,
					i);

			// Track the lowest distance, this is the BMU.
			if (distance < this.lowestDistance) {
				this.lowestDistance = distance;
				result = i;
			}
		}

		// Track the worst distance, this is the error for the entire network.
		if (this.lowestDistance > this.worstDistance) {
			this.worstDistance = this.lowestDistance;
		}

		return result;
	}

	/**
	 * Calculate the euclidean distance for the specified output neuron and the
	 * input vector.
	 * 
	 * @param synapse
	 *            The synapse to get the weights from.
	 * @param input
	 *            The input vector.
	 * @param outputNeuron
	 *            The neuron we are calculating the distance for.
	 * @return The euclidean distance.
	 */
	public double calculateEuclideanDistance(final Synapse synapse,
			final NeuralData input, final int outputNeuron) {
		double result = 0;
		for (int i = 0; i < input.size(); i++) {
			final double diff = input.getData(i)
					- synapse.getMatrix().get(i, outputNeuron);
			result += diff * diff;
		}
		return BoundMath.sqrt(result);
	}

	public double getWorstDistance() {
		return worstDistance;
	}

	public double getLowestDistance() {
		return lowestDistance;
	}
	
	
	
	
}
