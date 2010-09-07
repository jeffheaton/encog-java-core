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
package org.encog.neural.networks.structure;

import org.encog.engine.EngineMachineLearning;
import org.encog.engine.network.flat.ActivationFunctions;
import org.encog.engine.validate.BasicMachineLearningValidate;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.activation.ActivationLinear;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.activation.ActivationTANH;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.ContextLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.layers.RadialBasisFunctionLayer;
import org.encog.neural.networks.logic.FeedforwardLogic;

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
	 * @param network
	 *            The network to check.
	 * @return Null, if the net can not be flattened, an error message
	 *         otherwise.
	 */
	public String isValid(final EngineMachineLearning eml) {
		
		if( !(eml instanceof BasicNetwork) )
			return "Only a BasicNetwork can be converted to a flat network.";
		
		BasicNetwork network = (BasicNetwork)eml;
		
		final Layer inputLayer = network.getLayer(BasicNetwork.TAG_INPUT);
		final Layer outputLayer = network.getLayer(BasicNetwork.TAG_INPUT);

		if (inputLayer == null) {
			return "To convert to a flat network, there must be an input layer.";
		}

		if (outputLayer == null) {
			return "To convert to a flat network, there must be an output layer.";
		}
		
		if( network.getStructure().isConnectionLimited() ) {
			return "To convert to a flat network there can be no missing connections between layers.";
		}
		
		if( !(network.getLogic() instanceof FeedforwardLogic) ) {
			return "To convert to flat, must be using FeedforwardLogic or SimpleRecurrentLogic.";
		}

		for (final Layer layer : network.getStructure().getLayers()) {
			if (layer.getNext().size() > 2) {
				return "To convert to flat a network must have at most two outbound synapses.";
			}
			 
			if (layer.getClass()!=ContextLayer.class && layer.getClass()!=BasicLayer.class  && layer.getClass()!=RadialBasisFunctionLayer.class ) {
				return "To convert to flat a network must have only BasicLayer and ContextLayer layers.";
			}
		}
		return null;
	}



}
