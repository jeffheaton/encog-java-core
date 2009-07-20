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
package org.encog.util.network;

import org.encog.neural.activation.ActivationBiPolar;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.bipolar.BiPolarNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralDataMapping;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.WeightedSynapse;

public class BAMHolder {

	/**
	 * The Hopfield neural network.
	 */
	private final BasicNetwork network;

	/**
	 * The input layer.
	 */
	private final BasicLayer inputLayer;

	/**
	 * The output layer.
	 */
	private final BasicLayer outputLayer;

	private final Synapse synapseInputToOutput;
	private final Synapse synapseOutputToInput;

	public BAMHolder(int inputNeurons, int outputNeurons) {
		// construct the network
		this.network = new BasicNetwork();
		this.inputLayer = new BasicLayer(new ActivationBiPolar(), false,
				inputNeurons);
		this.outputLayer = new BasicLayer(new ActivationBiPolar(), false,
				outputNeurons);
		this.synapseInputToOutput = new WeightedSynapse(this.inputLayer,
				this.outputLayer);
		this.synapseOutputToInput = new WeightedSynapse(this.outputLayer,
				this.inputLayer);
		this.inputLayer.addSynapse(this.synapseInputToOutput);
		this.outputLayer.addSynapse(this.synapseOutputToInput);
		this.network.getStructure().finalizeStructure();
		this.network.setInputLayer(this.inputLayer);
		this.network.setOutputLayer(this.outputLayer);
	}

	public int getInputNeurons() {
		return this.inputLayer.getNeuronCount();
	}

	public int getOutputNeurons() {
		return this.outputLayer.getNeuronCount();
	}

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
	 * @return The Hopfield network.
	 */
	public BasicNetwork getNetwork() {
		return network;
	}

	private boolean propagateLayer(Synapse synapse, NeuralData input, NeuralData output) {
		int i, j;
		int sum, out = 0;
		boolean stable;

		stable = true;

		for (i = 0; i < synapse.getToNeuronCount(); i++) {
			sum = 0;
			for (j = 0; j < synapse.getFromNeuronCount(); j++) {
				sum += synapse.getMatrix().get(j,i) * input.getData(j);
			}
			if (sum != 0) {
				if (sum < 0)
					out = -1;
				else
					out = 1;
				if (out != (int)output.getData(i)) {
					stable = false;
					output.setData(i, out);
				}
			}
		}
		return stable;
	}
	
	private NeuralDataMapping duplicateMapping(NeuralDataMapping mapping)
	{
		return new NeuralDataMapping(
				new BiPolarNeuralData(mapping.getFrom().size()),
				new BiPolarNeuralData(mapping.getTo().size()));
	}

	public NeuralDataMapping compute(NeuralDataMapping input) {
		
			
		boolean stable1, stable2;

		do {
			stable1 = propagateLayer(this.synapseInputToOutput, input.getFrom(),input.getTo());
			stable2 = propagateLayer(this.synapseOutputToInput, input.getTo(),input.getFrom());
			//NeuralDataMapping.copy(output, currentInput);
		} while (!stable1 && !stable2);
		return null;
	}

}
