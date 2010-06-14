package org.encog.neural.pattern;

import org.encog.neural.activation.ActivationBiPolar;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.logic.BAMLogic;
import org.encog.neural.networks.svm.SVMNetwork;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.WeightedSynapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SVMPattern implements NeuralNetworkPattern {
	/**
	 * The number of neurons in the first layer.
	 */
	private int inputNeurons;

	/**
	 * The number of neurons in the second layer.
	 */
	private int outputNeurons;

	private boolean regression = true; 
	
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
		final String str = "A SVM network has no hidden layers.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);
	}

	/**
	 * Clear any settings on the pattern.
	 */
	public void clear() {
		this.inputNeurons = 0;
		this.outputNeurons = 0;

	}

	/**
	 * @return The generated network.
	 */
	public BasicNetwork generate() {
		final SVMNetwork network = new SVMNetwork(this.inputNeurons,this.outputNeurons,regression);
		return network;
	}

	/**
	 * Not used, the BAM uses a bipoloar activation function.
	 * 
	 * @param activation
	 *            Not used.
	 */
	public void setActivationFunction(final ActivationFunction activation) {
		final String str = "A SVM network can't specify a custom activation function.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);

	}

	


	public boolean isRegression() {
		return regression;
	}

	public void setRegression(boolean regression) {
		this.regression = regression;
	}

	public int getInputNeurons() {
		return inputNeurons;
	}

	public int getOutputNeurons() {
		return outputNeurons;
	}

	/**
	 * Set the number of input neurons.
	 * 
	 * @param count
	 *            The number of input neurons.
	 */
	public void setInputNeurons(final int count) {
			this.inputNeurons = count;
	}

	/**
	 * Set the number of output neurons.
	 * 
	 * @param count
	 *            The output neuron count.
	 */
	public void setOutputNeurons(final int count) {
		this.outputNeurons = count;
	}
}
