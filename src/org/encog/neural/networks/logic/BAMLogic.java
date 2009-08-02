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
package org.encog.neural.networks.logic;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralDataMapping;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the neural logic for an BAM type network.  See BAMPattern
 * for more information on this type of network.
 */
public class BAMLogic implements NeuralLogic  {
	
	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 246153941060562476L;

	/**
	 * The neural network.
	 */
	private BasicNetwork network;

	/**
	 * The input layer.
	 */
	private Layer inputLayer;

	/**
	 * The output layer.
	 */
	private Layer outputLayer;

	/**
	 * The connection between the input and output layer.
	 */
	private Synapse synapseInputToOutput;
	
	/**
	 * The connection between the output and the input layer.
	 */
	private Synapse synapseOutputToInput;
	
	/**
	 * The logging object.
	 */
	private static transient final Logger logger = LoggerFactory.getLogger(BAMLogic.class);


	/**
	 * @return The count of input neurons.
	 */
	public int getInputNeurons() {
		return this.inputLayer.getNeuronCount();
	}

	/**
	 * @return The count of output neurons.
	 */
	public int getOutputNeurons() {
		return this.outputLayer.getNeuronCount();
	}

	/**
	 * Add a pattern to the neural network.
	 * @param inputPattern The input pattern.
	 * @param outputPattern The output pattern(for this input).
	 */
	public void addPattern(final NeuralData inputPattern,
			final NeuralData outputPattern) {

		int weight;

		for (int i = 0; i < getInputNeurons(); i++) {
			for (int j = 0; j < getOutputNeurons(); j++) {
				weight = (int) (inputPattern.getData(i) * outputPattern
						.getData(j));
				this.synapseInputToOutput.getMatrix().add(i, j, weight);
				this.synapseOutputToInput.getMatrix().add(j, i, weight);
			}
		}

	}

	/**
	 * Clear any connection weights.
	 */
	public void clear() {
		this.synapseInputToOutput.getMatrix().clear();
		this.synapseOutputToInput.getMatrix().clear();
	}

	/**
	 * @return The network.
	 */
	public BasicNetwork getNetwork() {
		return network;
	}
	
	/**
	 * Get the specified weight.
	 * @param synapse The synapse to get the weight from.
	 * @param input The input, to obtain the size from.
	 * @param x The x matrix value. (could be row or column, depending on input)
	 * @param y The y matrix value. (could be row or column, depending on input)
	 * @return The value from the matrix.
	 */
	private double getWeight(Synapse synapse, NeuralData input, int x,int y)
	{
		if( synapse.getFromNeuronCount()!=input.size())
			return synapse.getMatrix().get(x, y);
		else
			return synapse.getMatrix().get(y, x);
	}

	/**
	 * Propagate the layer.
	 * @param synapse The synapse for this layer.
	 * @param input The input pattern.
	 * @param output The output pattern.
	 * @return True if the network has become stable.
	 */
	private boolean propagateLayer(Synapse synapse, NeuralData input,
			NeuralData output) {
		int i, j;
		int sum, out = 0;
		boolean stable;

		stable = true;

		for (i = 0; i < output.size(); i++) {
			sum = 0;
			for (j = 0; j < input.size(); j++) {
				sum += getWeight(synapse,input,i,j) * input.getData(j);
			}
			if (sum != 0) {
				if (sum < 0)
					out = -1;
				else
					out = 1;
				if (out != (int) output.getData(i)) {
					stable = false;
					output.setData(i, out);
				}
			}
		}
		return stable;
	}

	/**
	 * Compute the network for the specified input.
	 * @param input The input to the network.
	 * @return The output from the network.
	 */
	public NeuralDataMapping compute(NeuralDataMapping input) {

		boolean stable1 = true, stable2 = true;

		do {
			
				stable1 = propagateLayer(this.synapseInputToOutput,
						input.getFrom(), input.getTo());
				stable2 = propagateLayer(this.synapseOutputToInput, input.getTo(),
						input.getFrom());

			
		} while (!stable1 && !stable2);
		return null;
	}

	/**
	 * Setup the network logic, read parameters from the network.
	 * NOT USED, call compute(NeuralInputData).
	 * @param network The network that this logic class belongs to.
	 */
	public NeuralData compute(NeuralData input, NeuralOutputHolder useHolder) {
		String str = "Compute on BasicNetwork cannot be used, rather call" +
				" the compute(NeuralData) method on the BAMLogic.";
		if( logger.isErrorEnabled() )
		{
			logger.error(str);
		}
		throw new NeuralNetworkError(str);
	}

	/**
	 * Setup the network logic, read parameters from the network.
	 * @param network The network that this logic class belongs to.
	 */
	public void init(BasicNetwork network) {
		this.network = network;
		this.inputLayer = network.getInputLayer();
		this.outputLayer = network.getOutputLayer();
		this.synapseInputToOutput = network.getStructure().findSynapse(this.inputLayer, this.outputLayer, true);
		this.synapseOutputToInput =  network.getStructure().findSynapse(this.outputLayer, this.inputLayer, true);		
		
	}
	
	public String getLayerName(Layer layer) {
		if( layer==this.inputLayer )
			return "F1";
		else if( layer==this.outputLayer )
			return "F2";
		else
			return null;
	}
	
}
