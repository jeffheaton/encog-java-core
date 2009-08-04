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

import org.encog.neural.activation.ActivationCompetitive;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pattern that creates a CPN neural network.
 */
public class CPNPattern implements NeuralNetworkPattern {
	
	public final static String TAG_INSTAR = "INSTAR";
	public final static String TAG_OUTSTAR = "OUTSTAR";
	
	/**
	 * The number of neurons in the instar layer.
	 */
	int instarCount;
	
	/**
	 * The number of neurons in the outstar layer.
	 */
	int outstarCount;
	
	/**
	 * The number of neurons in the hidden layer.
	 */
	int inputCount;
	
	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Not used, will throw an error. CPN networks already have a predefined
	 * hidden layer called the instar layer.
	 */
	public void addHiddenLayer(int count) {
		final String str = 
			"A CPN already has a predefined hidden layer.  No additiona" +
			"specification is needed.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		
	}

	/**
	 * Clear any parameters that were set.
	 */
	public void clear() {
		this.inputCount = this.instarCount = this.outstarCount = 0;		
	}

	/**
	 * Generate the network.
	 * @return The generated network.
	 */
	public BasicNetwork generate() {
		
		Layer input,instar,outstar;
		int y = PatternConst.START_Y;
		
		BasicNetwork network = new BasicNetwork();
		network.addLayer(input = new BasicLayer(this.inputCount));
		network.addLayer(instar = new BasicLayer(new ActivationCompetitive(),true,this.instarCount));
		network.addLayer(outstar = new BasicLayer(this.outstarCount));
		network.getStructure().finalizeStructure();
		network.reset();
		
		input.setX(PatternConst.START_X);
		input.setY(y);
		y+=PatternConst.INC_Y;
		
		instar.setX(PatternConst.START_X);
		instar.setY(y);
		y+=PatternConst.INC_Y;
		
		outstar.setX(PatternConst.START_X);
		outstar.setY(y);
		
		// tag as needed
		network.tagLayer(BasicNetwork.TAG_INPUT, input);
		network.tagLayer(BasicNetwork.TAG_OUTPUT, outstar);
		network.tagLayer(CPNPattern.TAG_INSTAR, instar);
		network.tagLayer(CPNPattern.TAG_OUTSTAR, outstar);
		
		return network;
	}

	/**
	 * This method will throw an error.  The CPN network uses predefined
	 * activation functions.
	 */
	public void setActivationFunction(ActivationFunction activation) {
		final String str = 
			"A CPN network will use the BiPolar & competitive activation " 
			+ "functions, no activation function needs to be specified.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);
		
	}

	/**
	 * Set the number of input neurons.
	 */
	public void setInputNeurons(int count) {
		this.inputCount = count;
		
	}

	/**
	 * Set the number of output neurons.  Calling this method
	 * maps to setting the number of neurons in the outstar layer.
	 */
	public void setOutputNeurons(int count) {
		this.outstarCount = count;
		
	}

	/**
	 * Set the number of neurons in the instar layer.  This level
	 * is essentially a hidden layer.
	 * @param instarCount
	 */
	public void setInstarCount(int instarCount) {
		this.instarCount = instarCount;
	}

	/**
	 * Set the number of neurons in the outstar level, this level
	 * is mapped to the "output" level.
	 * @param outstarCount
	 */
	public void setOutstarCount(int outstarCount) {
		this.outstarCount = outstarCount;
	}
	
}
