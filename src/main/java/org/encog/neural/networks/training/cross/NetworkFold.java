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
package org.encog.neural.networks.training.cross;

import org.encog.neural.flat.FlatNetwork;
import org.encog.util.EngineArray;

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
	public final void copyToNetwork(final FlatNetwork target) {
		EngineArray.arrayCopy(this.weights, target.getWeights());
		EngineArray.arrayCopy(this.output, target.getLayerOutput());		
	}

	/**
	 * Copy the weights and output from the network.
	 * @param source The network to copy from.
	 */
	public final void copyFromNetwork(final FlatNetwork source) {
		EngineArray.arrayCopy(source.getWeights(), this.weights);
		EngineArray.arrayCopy(source.getLayerOutput(), this.output);
	}

	/**
	 * @return The network weights.
	 */
	public final double[] getWeights() {
		return weights;
	}

	/**
	 * @return The network output.
	 */
	public final double[] getOutput() {
		return output;
	}

}
