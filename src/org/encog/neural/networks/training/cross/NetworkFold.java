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
