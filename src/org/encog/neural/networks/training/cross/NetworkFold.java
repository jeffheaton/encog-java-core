package org.encog.neural.networks.training.cross;

import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.util.EngineArray;

/**
 * The network for one fold of a cross validation.
 */
public class NetworkFold {

	/**
	 * The weights for this fold.
	 */
	private final double[] weights;
	
	/**
	 * The output for this fold.
	 */
	private final double[] output;

	/**
	 * Construct a fold from the specified flat network.
	 * @param flat THe flat network.
	 */
	public NetworkFold(final FlatNetwork flat) {
		this.weights = EngineArray.arrayCopy(flat.getWeights());
		this.output = EngineArray.arrayCopy(flat.getLayerOutput());
	}

	/**
	 * Copy weights and output to the network.
	 * @param target The network to copy to.
	 */
	public void copyToNetwork(final FlatNetwork target) {
		EngineArray.arrayCopy(this.weights, target.getWeights());
		EngineArray.arrayCopy(this.output, target.getLayerOutput());		
	}

	/**
	 * Copy the weights and output from the network.
	 * @param source The network to copy from.
	 */
	public void copyFromNetwork(final FlatNetwork source) {
		EngineArray.arrayCopy(source.getWeights(), this.weights);
		EngineArray.arrayCopy(source.getLayerOutput(), this.output);
	}

	/**
	 * @return The network weights.
	 */
	public double[] getWeights() {
		return weights;
	}

	/**
	 * @return The network output.
	 */
	public double[] getOutput() {
		return output;
	}

}
