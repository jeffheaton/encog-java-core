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

package org.encog.neural.pattern;

import org.encog.engine.network.activation.ActivationBiPolar;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.logic.BAMLogic;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.WeightedSynapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Construct a Bidirectional Access Memory (BAM) neural network. This neural
 * network type learns to associate one pattern with another. The two patterns
 * do not need to be of the same length. This network has two that are connected
 * to each other. Though they are labeled as input and output layers to Encog,
 * they are both equal, and should simply be thought of as the two layers that
 * make up the net.
 * 
 */
public class BAMPattern implements NeuralNetworkPattern {

	/**
	 * The tag for the F1 layer.
	 */
	public static final String TAG_F1 = "F1";

	/**
	 * The tag for the F2 layer.
	 */
	public static final String TAG_F2 = "F2";

	/**
	 * The number of neurons in the first layer.
	 */
	private int f1Neurons;

	/**
	 * The number of neurons in the second layer.
	 */
	private int f2Neurons;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Unused, a BAM has no hidden layers.
	 * 
	 * @param count
	 *            Not used.
	 */
	public void addHiddenLayer(final int count) {
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
		this.f1Neurons = 0;
		this.f2Neurons = 0;

	}

	/**
	 * @return The generated network.
	 */
	public BasicNetwork generate() {
		final BasicNetwork network = new BasicNetwork(new BAMLogic());

		final Layer f1Layer = new BasicLayer(new ActivationBiPolar(), false,
				this.f1Neurons);
		final Layer f2Layer = new BasicLayer(new ActivationBiPolar(), false,
				this.f2Neurons);
		final Synapse synapseInputToOutput = new WeightedSynapse(f1Layer,
				f2Layer);
		final Synapse synapseOutputToInput = new WeightedSynapse(f2Layer,
				f1Layer);
		f1Layer.addSynapse(synapseInputToOutput);
		f2Layer.addSynapse(synapseOutputToInput);

		network.tagLayer(BAMPattern.TAG_F1, f1Layer);
		network.tagLayer(BAMPattern.TAG_F2, f2Layer);

		network.getStructure().finalizeStructure();
		network.getStructure().finalizeStructure();

		f1Layer.setY(PatternConst.START_Y);
		f2Layer.setY(PatternConst.START_Y);

		f1Layer.setX(PatternConst.START_X);
		f2Layer.setX(PatternConst.INDENT_X);

		return network;
	}

	/**
	 * Not used, the BAM uses a bipoloar activation function.
	 * 
	 * @param activation
	 *            Not used.
	 */
	public void setActivationFunction(final ActivationFunction activation) {
		final String str = "A BAM network can't specify a custom activation function.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);

	}

	/**
	 * Set the F1 neurons. The BAM really does not have an input and output
	 * layer, so this is simply setting the number of neurons that are in the
	 * first layer.
	 * 
	 * @param count
	 *            The number of neurons in the first layer.
	 */
	public void setF1Neurons(final int count) {
		this.f1Neurons = count;
	}

	/**
	 * Set the output neurons. The BAM really does not have an input and output
	 * layer, so this is simply setting the number of neurons that are in the
	 * second layer.
	 * 
	 * @param count
	 *            The number of neurons in the second layer.
	 */
	public void setF2Neurons(final int count) {
		this.f2Neurons = count;
	}

	/**
	 * Set the number of input neurons.
	 * 
	 * @param count
	 *            The number of input neurons.
	 */
	public void setInputNeurons(final int count) {
		final String str = "A BAM network has no input layer, consider setting F1 layer.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);

	}

	/**
	 * Set the number of output neurons.
	 * 
	 * @param count
	 *            The output neuron count.
	 */
	public void setOutputNeurons(final int count) {
		final String str = "A BAM network has no output layer, consider setting F2 layer.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);

	}

}
