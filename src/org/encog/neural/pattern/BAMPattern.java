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
package org.encog.neural.pattern;

import org.encog.neural.activation.ActivationBiPolar;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.WeightedSynapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Construct a Bidirectional Access Memory (BAM) neural network.  This
 * neural network type learns to associate one pattern with another.  The
 * two patterns do not need to be of the same length.  This network has two 
 * that are connected to each other.  Though they are labeled as input and
 * output layers to Encog, they are both equal, and should simply be thought
 * of as the two layers that make up the net.
 *
 */
public class BAMPattern implements NeuralNetworkPattern {

	/**
	 * The number of neurons in the first layer.
	 */
	private int inputNeurons;
	
	/**
	 * The number of neurons in the second layer.
	 */
	private int outputNeurons;
	
	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	

	/**
	 * Unused, a BAM has no hidden layers.
	 * @param count Not used.
	 */
	public void addHiddenLayer(int count) {
		final String str = "A BAM network has no hidden layers.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);		
	}

	/**
	 * Clear any settings on the pattern.
	 */
	public void clear() {
		this.inputNeurons = this.outputNeurons = 0;
		
	}

	/**
	 * @return The generated network.
	 */
	public BasicNetwork generate() {
		BasicNetwork network = new BasicNetwork();
		
		Layer inputLayer = new BasicLayer(new ActivationBiPolar(), false,
				inputNeurons);
		Layer outputLayer = new BasicLayer(new ActivationBiPolar(), false,
				outputNeurons);
		Synapse synapseInputToOutput = new WeightedSynapse(inputLayer,
				outputLayer);
		Synapse synapseOutputToInput = new WeightedSynapse(outputLayer,
				inputLayer);
		inputLayer.addSynapse(synapseInputToOutput);
		outputLayer.addSynapse(synapseOutputToInput);
		network.getStructure().finalizeStructure();
		network.setInputLayer(inputLayer);
		network.setOutputLayer(outputLayer);
		network.getStructure().finalizeStructure();
		
		inputLayer.setY(PatternConst.START_Y);
		outputLayer.setY(PatternConst.START_Y);
		
		inputLayer.setX(PatternConst.START_X);
		inputLayer.setX(PatternConst.INDENT_X);
		
		return network;
	}

	/**
	 * Not used, the BAM uses a bipoloar activation function.
	 * @param activation Not used.
	 */
	public void setActivationFunction(ActivationFunction activation) {
		final String str = "A BAM network can't specify a custom activation function.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);
		
	}

	/**
	 * Set the input neurons.  The BAM really does not have an input and output
	 * layer, so this is simply setting the number of neurons that are in the
	 * first layer.
	 * @param count The number of neurons in the first layer.
	 */
	public void setInputNeurons(int count) {
		this.inputNeurons = count;		
	}

	/**
	 * Set the output neurons.  The BAM really does not have an input and output
	 * layer, so this is simply setting the number of neurons that are in the
	 * second layer.
	 * @param count The number of neurons in the second layer.
	 */
	public void setOutputNeurons(int count) {
		this.outputNeurons = count;		
	}

}
