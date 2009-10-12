/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
