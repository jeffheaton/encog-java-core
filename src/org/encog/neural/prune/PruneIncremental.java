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
package org.encog.neural.prune;

import java.util.ArrayList;
import java.util.List;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.NeuralNetworkPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to help determine the optimal configuration for the hidden
 * layers of a neural network. It can accept a pattern, which specifies the type
 * of neural network to create, and a list of the maximum and minimum hidden
 * layer neurons. It will then attempt to train the neural network at all
 * configurations and see which hidden neuron counts work the best.
 * 
 * @author jheaton
 * 
 */
public class PruneIncremental {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The training set to use as different neural networks are evaluated.
	 */
	private final NeuralDataSet training;

	/**
	 * The pattern for which type of neural network we would like to create.
	 */
	private final NeuralNetworkPattern pattern;

	/**
	 * The ranges for the hidden layers.
	 */
	private final List<HiddenLayerParams> hidden = new ArrayList<HiddenLayerParams>();

	/**
	 * Keeps track of how many neurons in each hidden layer as training the
	 * evaluation progresses.
	 */
	private int[] hiddenCounts;

	/**
	 * Construct an object to determine the optimal number of hidden layers and
	 * neurons for the specified training data and pattern.
	 * 
	 * @param training
	 *            The training data to use.
	 * @param pattern
	 *            The network pattern to use to solve this data.
	 */
	public PruneIncremental(final NeuralDataSet training,
			final NeuralNetworkPattern pattern) {
		this.training = training;
		this.pattern = pattern;
	}

	/**
	 * Add a hidden layer's min and max. Call this once per hidden layer.
	 * Specify a zero min if it is possible to remove this hidden layer.
	 * 
	 * @param min
	 *            The minimum number of neurons for this layer.
	 * @param max
	 *            The maximum number of neurons for this layer.
	 */
	public void addHiddenLayer(final int min, final int max) {
		final HiddenLayerParams param = new HiddenLayerParams(min, max);
		this.hidden.add(param);
	}

	/**
	 * @return The hidden layer max and min.
	 */
	public List<HiddenLayerParams> getHidden() {
		return this.hidden;
	}

	/**
	 * @return The network pattern to use.
	 */
	public NeuralNetworkPattern getPattern() {
		return this.pattern;
	}

	/**
	 * @return The training set to use.
	 */
	public NeuralDataSet getTraining() {
		return this.training;
	}
	
	private BasicNetwork constructNetwork()
	{
		this.pattern.clear();
		return null;
	}

	/**
	 * Begin the process.
	 */
	public void prune() {

		if (this.hidden.size() == 0) {
			final String str = "To calculate the optimal hidden size, at least "
					+ "one hidden layer must be defined.";
			if (this.logger.isErrorEnabled()) {
				this.logger.error(str);
			}
		}

		this.hiddenCounts = new int[this.hidden.size()];
	}

}
