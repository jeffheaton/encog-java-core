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
package org.encog.neural.networks.structure;

import org.encog.engine.EngineMachineLearning;
import org.encog.engine.validate.BasicMachineLearningValidate;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.ContextLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.layers.RadialBasisFunctionLayer;
import org.encog.neural.networks.logic.FeedforwardLogic;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.neat.NEATSynapse;

/**
 * Only certain types of networks can be converted to a flat network. This class
 * validates this.
 */
public class ValidateForFlat extends BasicMachineLearningValidate {

	/**
	 * Determine if the specified neural network can be flat. If it can a null
	 * is returned, otherwise, an error is returned to show why the network
	 * cannot be flattened.
	 * 
	 * @param eml
	 *            The network to check.
	 * @return Null, if the net can not be flattened, an error message
	 *         otherwise.
	 */
	public String isValid(final EngineMachineLearning eml) {

		if (!(eml instanceof BasicNetwork)) {
			return "Only a BasicNetwork can be converted to a flat network.";
		}

		BasicNetwork network = (BasicNetwork) eml;

		final Layer inputLayer = network.getLayer(BasicNetwork.TAG_INPUT);
		final Layer outputLayer = network.getLayer(BasicNetwork.TAG_OUTPUT);

		if (inputLayer == null) {
			return "To convert to a flat network, there must be an input layer.";
		}

		if (outputLayer == null) {
			return "To convert to a flat network, there must be an output layer.";
		}

		for (final Layer layer : network.getStructure().getLayers()) {
			if (layer.getNext().size() > 2) {
				return "To convert to flat a network must have at most two outbound synapses.";
			}

			if (layer.getClass() != ContextLayer.class
					&& layer.getClass() != BasicLayer.class
					&& layer.getClass() != RadialBasisFunctionLayer.class) {
				return "To convert to flat a network must have only BasicLayer and ContextLayer layers.";
			}
		}

		for (final Synapse synapse : network.getStructure().getSynapses()) {
			if (synapse instanceof NEATSynapse) {
				return "A NEAT synapse cannot be flattened.";
			}
		}

		return null;
	}

}
