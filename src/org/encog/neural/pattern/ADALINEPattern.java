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

import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.activation.ActivationLinear;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.util.randomize.RangeRandomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Construct an adaline neural network.
 */
public class ADALINEPattern  implements NeuralNetworkPattern {

	/**
	 * The number of neurons in the input layer.
	 */
	private int inputNeurons;
	
	/**
	 * The number of neurons in the output layer.
	 */
	private int outputNeurons;
	
	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	/**
	 * Not used, the ADALINE has no hidden layers, this will throw an error.
	 */
	public void addHiddenLayer(int count) {
		final String str = "An ADALINE network has no hidden layers.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);	
		
	}

	/**
	 * Clear out any parameters.
	 */
	public void clear() {
		this.inputNeurons = this.outputNeurons = 0;		
	}

	/**
	 * Generate the network.
	 * @param The generated network.
	 */
	public BasicNetwork generate() {
		BasicNetwork network = new BasicNetwork();
		
		int y = PatternConst.START_Y;
		
		Layer inputLayer = new BasicLayer(new ActivationLinear(), false, inputNeurons );
		Layer outputLayer = new BasicLayer(new ActivationLinear(), true, outputNeurons );
		
		network.addLayer(inputLayer);
		network.addLayer(outputLayer);
		network.getStructure().finalizeStructure();
		
		(new RangeRandomizer(-0.5,0.5)).randomize(network);
		
		inputLayer.setX(PatternConst.START_X);
		inputLayer.setY(y);
		y += PatternConst.INC_Y;
		
		outputLayer.setX(PatternConst.START_X);
		outputLayer.setY(y);
		
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
	 * Set the input neurons.  
	 * @param count The number of neurons in the input layer.
	 */
	public void setInputNeurons(int count) {
		this.inputNeurons = count;		
	}

	/**
	 * Set the output neurons.  
	 * @param count The number of neurons in the output layer.
	 */
	public void setOutputNeurons(int count) {
		this.outputNeurons = count;		
	}

}
