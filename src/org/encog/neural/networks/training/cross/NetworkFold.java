package org.encog.neural.networks.training.cross;

import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.util.EngineArray;

public class NetworkFold {

	final double[] weights;
	final double[] output;

	public NetworkFold(FlatNetwork flat) {
		this.weights = EngineArray.arrayCopy(flat.getWeights());
		this.output = EngineArray.arrayCopy(flat.getLayerOutput());
	}

	public void copyToNetwork(FlatNetwork target) {
		EngineArray.arrayCopy(this.weights, target.getWeights());
		EngineArray.arrayCopy(this.output, target.getLayerOutput());		
	}

	public void copyFromNetwork(FlatNetwork source) {
		EngineArray.arrayCopy(source.getWeights(), this.weights);
		EngineArray.arrayCopy(source.getLayerOutput(), this.output);
	}

	/**
	 * @return the weights
	 */
	public double[] getWeights() {
		return weights;
	}

	/**
	 * @return the output
	 */
	public double[] getOutput() {
		return output;
	}

}
