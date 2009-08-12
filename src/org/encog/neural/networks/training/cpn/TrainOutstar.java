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
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.LearningRate;

/**
 * Used for Instar training of a CPN neural network. A CPN network is a hybrid
 * supervised/unsupervised network. The Outstar training handles the supervised
 * portion of the training.
 * 
 */
public class TrainOutstar extends BasicTraining implements LearningRate {

	/**
	 * The learning rate.
	 */
	private double learningRate;
	
	/**
	 * The network being trained.
	 */
	private BasicNetwork network;
	
	/**
	 * The training data.  Supervised training, so both input and ideal must
	 * be provided.
	 */
	private NeuralDataSet training;
	
	/**
	 * If the weights have not been initialized, then they must be initialized
	 * before training begins. This will be done on the first iteration.
	 */
	private boolean mustInit = true;
	
	/**
	 * The parts of this CPN network.
	 */
	private FindCPN parts;

	/**
	 * Construct the outstar trainer.
	 * @param network The network to train.
	 * @param training The training data, must provide ideal outputs.
	 * @param learningRate The learning rate.
	 */
	public TrainOutstar(BasicNetwork network, NeuralDataSet training,
			double learningRate) {
		this.network = network;
		this.training = training;
		this.learningRate = learningRate;
		this.parts = new FindCPN(this.network);
	}

	/**
	 * @return The learning rate.
	 */
	public double getLearningRate() {
		return this.learningRate;
	}

	/**
	 * Set the learning rate.
	 * @return The new learning rate.
	 */
	public void setLearningRate(double rate) {
		this.learningRate = rate;
	}

	/**
	 * @return The network being trained.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * Approximate the weights based on the input values.
	 */
	private void initWeight() {
		for (int i = 0; i < this.parts.getOutstarLayer().getNeuronCount(); i++) {
			int j = 0;
			for (NeuralDataPair pair : this.training) {
				this.parts.getOutstarSynapse().getMatrix().set(j++, i,
						pair.getIdeal().getData(i));
			}
		}
		this.mustInit = false;
	}

	/**
	 * Perform one training iteration.
	 */
	public void iteration() {

		if (this.mustInit)
			initWeight();

		for (NeuralDataPair pair : this.training) {
			NeuralData out = this.parts.getInstarSynapse().compute(
					pair.getInput());
			int j = this.parts.winner(out);
			for (int i = 0; i < this.parts.getOutstarLayer().getNeuronCount(); i++) {
				double delta = this.learningRate
						* (pair.getIdeal().getData(i) - this.parts
								.getOutstarSynapse().getMatrix().get(j, i));
				this.parts.getOutstarSynapse().getMatrix().add(j, i, delta);
			}
		}
	}
}
