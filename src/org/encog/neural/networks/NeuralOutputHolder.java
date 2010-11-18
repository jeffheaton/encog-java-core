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
package org.encog.neural.networks;

import java.util.HashMap;
import java.util.Map;

import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds the output from each layer of the neural network. This is very useful
 * for the propagation algorithms that need to examine the output of each
 * individual layer.
 * 
 * @author jheaton
 * 
 */
public class NeuralOutputHolder {

	/**
	 * The results from each of the synapses.
	 */
	private final Map<Synapse, NeuralData> result;

	/**
	 * The output from the entire neural network.
	 */
	private NeuralData output;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct an empty holder.
	 */
	public NeuralOutputHolder() {
		this.result = new HashMap<Synapse, NeuralData>();
	}

	/**
	 * @return The output from the neural network.
	 */
	public NeuralData getOutput() {
		return this.output;
	}

	/**
	 * @return The result from the synapses in a map.
	 */
	public Map<Synapse, NeuralData> getResult() {
		return this.result;
	}

	/**
	 * Set the output.
	 * 
	 * @param output
	 *            The new output.
	 */
	public void setOutput(final NeuralData output) {
		this.output = output;
	}

}
