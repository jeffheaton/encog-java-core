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
package org.encog.neural.networks.training.propagation.back;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.Momentum;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.strategy.SmartLearningRate;
import org.encog.neural.networks.training.strategy.SmartMomentum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a backpropagation training algorithm
 * for feed forward neural networks. It is used in the same manner as any other
 * training class that implements the Train interface.
 * 
 * Backpropagation is a common neural network training algorithm. It works by
 * analyzing the error of the output of the neural network. Each neuron in the
 * output layer's contribution, according to weight, to this error is
 * determined. These weights are then adjusted to minimize this error. This
 * process continues working its way backwards through the layers of the neural
 * network.
 * 
 * This implementation of the backpropagation algorithm uses both momentum and a
 * learning rate. The learning rate specifies the degree to which the weight
 * matrixes will be modified through each iteration. The momentum specifies how
 * much the previous learning iteration affects the current. To use no momentum
 * at all specify zero.
 * 
 * One primary problem with backpropagation is that the magnitude of the partial
 * derivative is often detrimental to the training of the neural network.  The
 * other propagation methods of Manhatten and Resilient address this issue in different
 * ways.  In general, it is suggested that you use the resilient propagation technique
 * for most Encog training tasks over back propagation.
 */
public class Backpropagation extends Propagation implements Momentum, LearningRate {

	/**
	 * The momentum, this is the degree to which the previous training cycle
	 * affects the current one.
	 */
	private double momentum;
	
	private double learningRate;

	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 
	 * @param network
	 * @param training
	 * @param learnRate
	 *            The rate at which the weight matrix will be adjusted based on
	 *            learning.
	 * @param momentum
	 *            The influence that previous iteration's training deltas will
	 *            have on the current iteration.
	 */
	public Backpropagation(BasicNetwork network, NeuralDataSet training,
			double learnRate, double momentum) {
		super(network, new BackpropagationMethod(), training);
		this.momentum = momentum;
		this.learningRate = learnRate;
	}
	
	/**
	 * 
	 * @param network
	 * @param training
	 */
	public Backpropagation(final BasicNetwork network,
			final NeuralDataSet training) {
		this(network, training, 0, 0);
		addStrategy(new SmartLearningRate());
		addStrategy(new SmartMomentum());
	}

	public double getMomentum() {
		return this.momentum;
	}

	public void setMomentum(double m) {
		this.momentum = m;
	}

	public double getLearningRate() {
		return this.learningRate;
	}

	public void setLearningRate(double rate) {
		this.learningRate = rate;
	}
	
}
