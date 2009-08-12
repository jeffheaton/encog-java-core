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

package org.encog.neural.networks.training.cpn;

import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.pattern.CPNPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Find the parts of a CPN network.
 */
public class FindCPN {

	/**
	 * The input layer.
	 */
	private Layer inputLayer;

	/**
	 * The instar layer.
	 */
	private Layer instarLayer;

	/**
	 * The outstar layer.
	 */
	private Layer outstarLayer;

	/**
	 * The synapse from the input to instar layer.
	 */
	private Synapse instarSynapse;

	/**
	 * The synapse from the instar to the outstar layer.
	 */
	private Synapse outstarSynapse;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct the object and find the parts of the network.
	 * 
	 * @param network
	 *            The network to train.
	 */
	public FindCPN(BasicNetwork network) {
		if (network.getStructure().getLayers().size() != 3) {
			String str = "A CPN network must have exactly 3 layers";
			if (logger.isErrorEnabled()) {
				logger.error(str);
			}
			throw new TrainingError(str);
		}

		this.inputLayer = network.getLayer(BasicNetwork.TAG_INPUT);
		this.outstarLayer = network.getLayer(CPNPattern.TAG_OUTSTAR);
		this.instarLayer = network.getLayer(CPNPattern.TAG_INSTAR);

		if (this.outstarLayer == null) {
			String str = "Can't find an OUTSTAR layer, this is required.";
			if (logger.isErrorEnabled()) {
				logger.error(str);
			}
			throw new TrainingError(str);
		}

		if (this.instarLayer == null) {
			String str = "Can't find an OUTSTAR layer, this is required.";
			if (logger.isErrorEnabled()) {
				logger.error(str);
			}
			throw new TrainingError(str);
		}

		this.instarSynapse = this.inputLayer.getNext().iterator().next();
		this.outstarSynapse = this.instarLayer.getNext().iterator().next();
	}

	/**
	 * @return The input layer.
	 */
	public Layer getInputLayer() {
		return inputLayer;
	}

	/**
	 * @return The instar layer.
	 */
	public Layer getInstarLayer() {
		return instarLayer;
	}

	/**
	 * @return The outstar layer.
	 */
	public Layer getOutstarLayer() {
		return outstarLayer;
	}

	/**
	 * @return The instar synapse.
	 */
	public Synapse getInstarSynapse() {
		return instarSynapse;
	}

	/**
	 * @return The outstar synapse.
	 */
	public Synapse getOutstarSynapse() {
		return outstarSynapse;
	}

	/**
	 * Calculate the winning neuron from the data, this is the neuron
	 * that has the highest output.
	 * @param data The data to use to determine the winning neuron.
	 * @return The winning neuron index, or -1 if no winner.
	 */
	public int winner(NeuralData data) {
		int winner = -1;

		for (int i = 0; i < data.size(); i++) {
			if (winner == -1 || data.getData(i) > data.getData(winner)) {
				winner = i;
			}
		}

		return winner;
	}

}
