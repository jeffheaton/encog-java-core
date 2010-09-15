/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.neural.networks.training.propagation.back;

import org.encog.engine.network.train.prop.TrainFlatNetworkBackPropagation;
import org.encog.engine.network.train.prop.TrainFlatNetworkOpenCL;
import org.encog.engine.network.train.prop.TrainFlatNetworkResilient;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.engine.util.EngineArray;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.Momentum;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
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
public class Backpropagation extends Propagation implements Momentum,
		LearningRate {

	/**
	 * The resume key for backpropagation.
	 */
	public final static String LAST_DELTA = "LAST_DELTA";
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Create a class to train using backpropagation.  Use auto learn rate and momentum.  Use the CPU to train.
	 * 
	 * @param network
	 *            The network that is to be trained.
	 * @param training
	 *            The training data to be used for backpropagation.
	 */
	public Backpropagation(final BasicNetwork network,
			final NeuralDataSet training) {
		this(network, training, null, 0, 0);
		addStrategy(new SmartLearningRate());
		addStrategy(new SmartMomentum());
	}

	/**
	 * Train using the specified learning rate and momentum.  Use the CPU to train.
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
			final double momentum)
	{
		this(network,training,null,learnRate,momentum);
	}

	/**
	 * 
	 * @param network
	 *            The network that is to be trained
	 * @param training
	 *            The training set
	 * @param device The device to use, null for CPU.
	 * @param learnRate
	 *            The rate at which the weight matrix will be adjusted based on
	 *            learning.
	 * @param momentum
	 *            The influence that previous iteration's training deltas will
	 *            have on the current iteration.
	 */
	public Backpropagation(final BasicNetwork network,
			final NeuralDataSet training, final EncogCLDevice device, final double learnRate,
			final double momentum) {
		super(network, training);
		
		if (device == null) {
			TrainFlatNetworkBackPropagation backFlat = new TrainFlatNetworkBackPropagation(
					network.getStructure().getFlat(),
					this.getTraining(),
					learnRate,
					momentum);
			this.setFlatTraining(backFlat);
		} else {
			TrainFlatNetworkOpenCL rpropFlat = new TrainFlatNetworkOpenCL(
					network.getStructure().getFlat(), this.getTraining(),
					device);
			rpropFlat.learnBPROP(learnRate, momentum);
			this.setFlatTraining(rpropFlat);
		}
		

	}

	/**
	 * @return The learning rate, this is value is essentially a percent. It is
	 *         the degree to which the gradients are applied to the weight
	 *         matrix to allow learning.
	 */
	public double getLearningRate() {
		return ((TrainFlatNetworkBackPropagation)this.getFlatTraining()).getLearningRate();
	}

	/**
	 * @return The momentum for training. This is the degree to which changes
	 *         from which the previous training iteration will affect this
	 *         training iteration. This can be useful to overcome local minima.
	 */
	public double getMomentum() {
		return ((TrainFlatNetworkBackPropagation)this.getFlatTraining()).getMomentum();
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
		((TrainFlatNetworkBackPropagation)this.getFlatTraining()).setLearningRate(rate);
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
		((TrainFlatNetworkBackPropagation)this.getFlatTraining()).setLearningRate(m);
	}
	
	/**
	 * Determine if the specified continuation object is valid to resume with.
	 * @param state The continuation object to check.
	 * @return True if the specified continuation object is valid for this
	 * training method and network.
	 */
	public boolean isValidResume(final TrainingContinuation state) {
		if (!state.getContents().containsKey(Backpropagation.LAST_DELTA)) {
			return false;
		}

		final double[] d = (double[]) state
				.get(Backpropagation.LAST_DELTA);
		return d.length == getNetwork().getStructure().calculateSize();
	}
	
	/**
	 * Pause the training.
	 * @return A training continuation object to continue with.
	 */
	public TrainingContinuation pause() {
		final TrainingContinuation result = new TrainingContinuation();
		
		TrainFlatNetworkBackPropagation backFlat = (TrainFlatNetworkBackPropagation)getFlatTraining();
		double[] d = backFlat.getLastDelta();
		result.set(Backpropagation.LAST_DELTA, d);
		return result;
	}
	
	/**
	 * Resume training.
	 * @param state The training state to return to.
	 */
	public void resume(final TrainingContinuation state) {
		if (!isValidResume(state)) {
			throw new TrainingError("Invalid training resume data length");
		}
		
		((TrainFlatNetworkBackPropagation)this.getFlatTraining()).setLastDelta(
			(double[]) state.get(Backpropagation.LAST_DELTA));
		
	}

	public double[] getLastDelta() {
		return ((TrainFlatNetworkBackPropagation)this.getFlatTraining()).getLastDelta();
	}
}
