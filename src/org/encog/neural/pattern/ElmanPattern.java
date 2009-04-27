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
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.ContextLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.SynapseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElmanPattern implements NeuralNetworkPattern {

	private int inputNeurons;
	private int outputNeurons;
	private int hiddenNeurons;
	private ActivationFunction activation;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	public ElmanPattern() {
		this.inputNeurons = -1;
		this.outputNeurons = -1;
		this.hiddenNeurons = -1;
	}

	public void addHiddenLayer(final int count) {
		if (this.hiddenNeurons != -1) {
			final String str = "An Elman neural network should have only one hidden layer.";
			if (this.logger.isErrorEnabled()) {
				this.logger.error(str);
			}
			throw new PatternError(str);
		}

		this.hiddenNeurons = count;

	}

	public BasicNetwork generate() {
		int y = 50;
		Layer hidden, input, output;
		final Layer context = new ContextLayer(this.hiddenNeurons);
		final BasicNetwork network = new BasicNetwork();
		network.addLayer(input = new BasicLayer(this.inputNeurons));
		input.setX(50);
		input.setY(y);
		y += 150;
		network.addLayer(hidden = new BasicLayer(this.hiddenNeurons));
		hidden.setX(50);
		hidden.setY(y);
		context.setX(300);
		context.setY(y);
		y += 150;
		hidden.addNext(context, SynapseType.OneToOne);
		context.addNext(hidden);
		network.addLayer(output = new BasicLayer(this.outputNeurons));
		output.setX(50);
		output.setY(y);
		network.getStructure().finalizeStructure();
		network.reset();
		return network;
	}

	public void setActivationFunction(final ActivationFunction activation) {
		this.activation = activation;
	}

	public void setInputNeurons(final int count) {
		this.inputNeurons = count;
	}

	public void setOutputNeurons(final int count) {
		this.outputNeurons = count;
	}

}
