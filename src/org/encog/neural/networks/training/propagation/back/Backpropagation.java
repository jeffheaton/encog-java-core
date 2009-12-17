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

import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.Momentum;
import org.encog.neural.networks.training.propagation.PropagateErrors;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.strategy.SmartLearningRate;
import org.encog.neural.networks.training.strategy.SmartMomentum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a backpropagation training algorithm for feed forward
 * neural networks. It is used in the same manner as any other training class
 * that implements the Train interface.
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
 * derivative is often detrimental to the training of the neural network. The
 * other propagation methods of Manhatten and Resilient address this issue in
 * different ways. In general, it is suggested that you use the resilient
 * propagation technique for most Encog training tasks over back propagation.
 */
public class Backpropagation extends BasicTraining implements Momentum,
		LearningRate {

	/**
	 * The momentum, this is the degree to which the previous training cycle
	 * affects the current one.
	 */
	private double momentum;

	/**
	 * The learning rate, this is value is essentially a percent. It is the
	 * degree to which the gradients are applied to the weight matrix to allow
	 * learning.
	 */
	private double learningRate;
	
	private BasicNetwork network;
	private NeuralDataSet training;
	private double[] lastDelta;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Create a class to train using backpropagation.
	 * 
	 * @param network
	 *            The network that is to be trained.
	 * @param training
	 *            The training data to be used for backpropagation.
	 */
	public Backpropagation(final BasicNetwork network,
			final NeuralDataSet training) {
		this(network, training, 0, 0);
		addStrategy(new SmartLearningRate());
		addStrategy(new SmartMomentum());
	}

	/**
	 * 
	 * @param network
	 *            The network that is to be trained
	 * @param training
	 *            The training set
	 * @param learnRate
	 *            The rate at which the weight matrix will be adjusted based on
	 *            learning.
	 * @param momentum
	 *            The influence that previous iteration's training deltas will
	 *            have on the current iteration.
	 */
	public Backpropagation(final BasicNetwork network,
			final NeuralDataSet training, final double learnRate,
			final double momentum) {
		
		this.momentum = momentum;
		this.learningRate = learnRate;
		this.network = network;
		this.training = training;
		this.lastDelta = new double[network.getStructure().calculateSize()];
	}

	/**
	 * @return The learning rate, this is value is essentially a percent. It is
	 *         the degree to which the gradients are applied to the weight
	 *         matrix to allow learning.
	 */
	public double getLearningRate() {
		return this.learningRate;
	}

	/**
	 * @return The momentum for training. This is the degree to which changes
	 *         from which the previous training iteration will affect this
	 *         training iteration. This can be useful to overcome local minima.
	 */
	public double getMomentum() {
		return this.momentum;
	}

	/**
	 * Set the learning rate, this is value is essentially a percent. It is the
	 * degree to which the gradients are applied to the weight matrix to allow
	 * learning.
	 * 
	 * @param rate
	 *            The learning rate.
	 */
	public void setLearningRate(final double rate) {
		this.learningRate = rate;
	}

	/**
	 * Set the momentum for training. This is the degree to which changes from
	 * which the previous training iteration will affect this training
	 * iteration. This can be useful to overcome local minima.
	 * 
	 * @param m
	 *            The momentum.
	 */
	public void setMomentum(final double m) {
		this.momentum = m;
	}

	@Override
	public BasicNetwork getNetwork() {
		return this.network;
	}

	@Override
	public void iteration() {
		PropagateErrors prop = new PropagateErrors(this.network);
		
		double[] weights = NetworkCODEC.networkToArray(network);		
		
		
		prop.calculate(this.training,weights);
		double[] errors = prop.getErrors();
		
		for(int i=0;i<errors.length;i++) {
			double last = lastDelta[i]; 
			lastDelta[i] = (errors[i]*this.learningRate)+(last*this.momentum);
			weights[i]+=lastDelta[i];
		}
		NetworkCODEC.arrayToNetwork(weights, this.network);
		
		this.setError(prop.getError());
		
		
	}

}
