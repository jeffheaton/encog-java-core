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

package org.encog.neural.networks.training.propagation;

import java.util.ArrayList;
import java.util.List;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.util.ErrorCalculation;
import org.encog.util.logging.DumpMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements basic functionality that is needed by each of the propagation
 * methods. The specifics of each of the propagation methods is implemented
 * inside of the PropagationMethod interface implementors.
 * 
 * @author jheaton
 * 
 */
public class Propagation extends BasicTraining {


	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The batch size. Defaults to the max size of an integer, which means
	 * update once per iteration.
	 * 
	 * The batch size is the frequency with which the weights are updated per
	 * iteration. Setting it to the size of the training set means one update
	 * per iteration. Setting this to a lower number may improve training
	 * efficiency at the cost of processing time.
	 * 
	 * If you do not want to use batch training, specify a value of 1, then the
	 * weights will be updated on each iteration, which is online training.
	 */
	private int batchSize = Integer.MAX_VALUE;
	
	private PropagationUtil propagationUtil;

	/**
	 * Construct a propagation trainer.
	 * 
	 * @param network
	 *            The network to train.
	 * @param method
	 *            The propagation method to use.
	 * @param training
	 *            The training data to use.
	 * 
	 */
	public Propagation(final BasicNetwork network,
			final PropagationMethod method, final NeuralDataSet training) {
		
		this.propagationUtil = new PropagationUtil(network, method);				
		setTraining(training);
	}




	/**
	 * Perform one iteration of training.
	 * 
	 * Note: if you get a StackOverflowError while training, then you have
	 * endless recurrent loops. Try inserting no trainable synapses on one side
	 * of the loop.
	 */
	public void iteration() {

		if (this.logger.isInfoEnabled()) {
			this.logger.info("Beginning propagation iteration");
		}

		preIteration();

		final ErrorCalculation errorCalculation = new ErrorCalculation();

		int processedCount = 0;

		for (final NeuralDataPair pair : getTraining()) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug(
						"Backpropagation training on: input={},ideal={}", pair
								.getInput(), pair.getIdeal());
			}
			final NeuralData actual = this.propagationUtil.forwardPass(pair.getInput());

			errorCalculation.updateError(actual, pair.getIdeal());
			this.propagationUtil.backwardPass(pair.getIdeal());

			processedCount++;
			if (processedCount >= this.batchSize) {
				processedCount = 0;
				this.propagationUtil.getMethod().learn();
			}
		}

		if (processedCount != 0) {
			this.propagationUtil.getMethod().learn();
		}

		setError(errorCalculation.calculateRMS());

		postIteration();
	}

	/**
	 * @return Get the batch size. See batchSize property for a complete 
	 * description.
	 */
	public int getBatchSize() {
		return batchSize;
	}

	/**
	 * Set the batch size. See batchSize property for a complete description.
	 * @param batchSize The batch training size.
	 */
	public void setBatchSize(final int batchSize) {
		this.batchSize = batchSize;
	}

	public BasicNetwork getNetwork() {
		return this.propagationUtil.getNetwork();
	}

	public PropagationUtil getPropagationUtil() {
		return propagationUtil;
	}	
}
