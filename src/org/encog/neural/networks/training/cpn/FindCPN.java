/*
 * Encog(tm) Core v2.5 - Java Version
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
	private final Layer inputLayer;

	/**
	 * The instar layer.
	 */
	private final Layer instarLayer;

	/**
	 * The outstar layer.
	 */
	private final Layer outstarLayer;

	/**
	 * The synapse from the input to instar layer.
	 */
	private final Synapse instarSynapse;

	/**
	 * The synapse from the instar to the outstar layer.
	 */
	private final Synapse outstarSynapse;

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
	public FindCPN(final BasicNetwork network) {
		if (network.getStructure().getLayers().size() != 3) {
			final String str = "A CPN network must have exactly 3 layers";
			if (this.logger.isErrorEnabled()) {
				this.logger.error(str);
			}
			throw new TrainingError(str);
		}

		this.inputLayer = network.getLayer(BasicNetwork.TAG_INPUT);
		this.outstarLayer = network.getLayer(CPNPattern.TAG_OUTSTAR);
		this.instarLayer = network.getLayer(CPNPattern.TAG_INSTAR);

		if (this.outstarLayer == null) {
			final String str = "Can't find an OUTSTAR layer, this is required.";
			if (this.logger.isErrorEnabled()) {
				this.logger.error(str);
			}
			throw new TrainingError(str);
		}

		if (this.instarLayer == null) {
			final String str = "Can't find an OUTSTAR layer, this is required.";
			if (this.logger.isErrorEnabled()) {
				this.logger.error(str);
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
		return this.inputLayer;
	}

	/**
	 * @return The instar layer.
	 */
	public Layer getInstarLayer() {
		return this.instarLayer;
	}

	/**
	 * @return The instar synapse.
	 */
	public Synapse getInstarSynapse() {
		return this.instarSynapse;
	}

	/**
	 * @return The outstar layer.
	 */
	public Layer getOutstarLayer() {
		return this.outstarLayer;
	}

	/**
	 * @return The outstar synapse.
	 */
	public Synapse getOutstarSynapse() {
		return this.outstarSynapse;
	}

	/**
	 * Calculate the winning neuron from the data, this is the neuron that has
	 * the highest output.
	 * 
	 * @param data
	 *            The data to use to determine the winning neuron.
	 * @return The winning neuron index, or -1 if no winner.
	 */
	public int winner(final NeuralData data) {
		int winner = -1;

		for (int i = 0; i < data.size(); i++) {
			if ((winner == -1) || (data.getData(i) > data.getData(winner))) {
				winner = i;
			}
		}

		return winner;
	}

}
