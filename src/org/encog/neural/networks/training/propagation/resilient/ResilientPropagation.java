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

package org.encog.neural.networks.training.propagation.resilient;

import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.network.train.TrainFlatNetwork;
import org.encog.engine.network.train.prop.RPROPConst;
import org.encog.engine.network.train.prop.TrainFlatNetworkOpenCL;
import org.encog.engine.network.train.prop.TrainFlatNetworkResilient;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.engine.util.EngineArray;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

/**
 * One problem with the backpropagation algorithm is that the magnitude of the
 * partial derivative is usually too large or too small. Further, the learning
 * rate is a single value for the entire neural network. The resilient
 * propagation learning algorithm uses a special update value(similar to the
 * learning rate) for every neuron connection. Further these update values are
 * automatically determined, unlike the learning rate of the backpropagation
 * algorithm.
 * 
 * For most training situations, we suggest that the resilient propagation
 * algorithm (this class) be used for training.
 * 
 * There are a total of three parameters that must be provided to the resilient
 * training algorithm. Defaults are provided for each, and in nearly all cases,
 * these defaults are acceptable. This makes the resilient propagation algorithm
 * one of the easiest and most efficient training algorithms available.
 * 
 * The optional parameters are:
 * 
 * zeroTolerance - How close to zero can a number be to be considered zero. The
 * default is 0.00000000000000001.
 * 
 * initialUpdate - What are the initial update values for each matrix value. The
 * default is 0.1.
 * 
 * maxStep - What is the largest amount that the update values can step. The
 * default is 50.
 * 
 * 
 * Usually you will not need to use these, and you should use the constructor
 * that does not require them.
 * 
 * 
 * @author jheaton
 * 
 */
public class ResilientPropagation extends Propagation {

	/**
	 * Continuation tag for the last gradients.
	 */
	public static final String LAST_GRADIENTS = "LAST_GRADIENTS";

	/**
	 * Continuation tag for the last values.
	 */
	public static final String UPDATE_VALUES = "UPDATE_VALUES";

	/**
	 * Construct a resilient training object. Use the defaults for all training
	 * parameters. Usually this is the constructor to use as the resilient
	 * training algorithm is designed for the default parameters to be
	 * acceptable for nearly all problems. Use the CPU to train.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training set to use.
	 */
	public ResilientPropagation(final BasicNetwork network,
			final NeuralDataSet training) {
		this(network, training, null, RPROPConst.DEFAULT_ZERO_TOLERANCE,
				RPROPConst.DEFAULT_INITIAL_UPDATE, RPROPConst.DEFAULT_MAX_STEP);
	}

	/**
	 * Construct an RPROP trainer, allows an OpenCL device to be specified. Use
	 * the defaults for all training parameters. Usually this is the constructor
	 * to use as the resilient training algorithm is designed for the default
	 * parameters to be acceptable for nearly all problems.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 * @param device
	 *            The device to use.
	 */
	public ResilientPropagation(final BasicNetwork network,
			final NeuralDataSet training, EncogCLDevice device) {
		this(network, training, device, RPROPConst.DEFAULT_ZERO_TOLERANCE,
				RPROPConst.DEFAULT_INITIAL_UPDATE, RPROPConst.DEFAULT_MAX_STEP);
	}

	/**
	 * Construct a resilient training object, allow the training parameters to
	 * be specified. Usually the default parameters are acceptable for the
	 * resilient training algorithm. Therefore you should usually use the other
	 * constructor, that makes use of the default values.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training set to use.
	 * @param zeroTolerance
	 *            The zero tolerance.
	 * @param initialUpdate
	 *            The initial update values, this is the amount that the deltas
	 *            are all initially set to.
	 * @param maxStep
	 *            The maximum that a delta can reach.
	 */
	public ResilientPropagation(final BasicNetwork network,
			final NeuralDataSet training, EncogCLDevice device,
			final double zeroTolerance, final double initialUpdate,
			final double maxStep) {

		super(network, training);

		if (device == null) {
			TrainFlatNetworkResilient rpropFlat = new TrainFlatNetworkResilient(
					network.getStructure().getFlat(), this.getTraining());
			this.setFlatTraining(rpropFlat);
		} else {
			TrainFlatNetworkOpenCL rpropFlat = new TrainFlatNetworkOpenCL(
					network.getStructure().getFlat(), this.getTraining(),
					device);
			rpropFlat.learnRPROP(initialUpdate, maxStep);
			this.setFlatTraining(rpropFlat);
		}

	}

	/**
	 * @return True, as RPROP can continue.
	 */
	public boolean canContinue() {
		return true;
	}

	/**
	 * Determine if the specified continuation object is valid to resume with.
	 * 
	 * @param state
	 *            The continuation object to check.
	 * @return True if the specified continuation object is valid for this
	 *         training method and network.
	 */
	public boolean isValidResume(final TrainingContinuation state) {
		if (!state.getContents().containsKey(
				ResilientPropagation.LAST_GRADIENTS)
				|| !state.getContents().containsKey(
						ResilientPropagation.UPDATE_VALUES)) {
			return false;
		}

		final double[] d = (double[]) state
				.get(ResilientPropagation.LAST_GRADIENTS);
		return d.length == getNetwork().getStructure().calculateSize();
	}

	/**
	 * Pause the training.
	 * 
	 * @return A training continuation object to continue with.
	 */
	public TrainingContinuation pause() {
		final TrainingContinuation result = new TrainingContinuation();

		result.set(ResilientPropagation.LAST_GRADIENTS,
				((TrainFlatNetworkResilient) this.getFlatTraining())
						.getLastGradient());
		result.set(ResilientPropagation.UPDATE_VALUES,
				((TrainFlatNetworkResilient) this.getFlatTraining())
						.getUpdateValues());
		return result;
	}

	/**
	 * Resume training.
	 * 
	 * @param state
	 *            The training state to return to.
	 */
	public void resume(final TrainingContinuation state) {
		if (!isValidResume(state)) {
			throw new TrainingError("Invalid training resume data length");
		}
		double[] lastGradient = (double[]) state
				.get(ResilientPropagation.LAST_GRADIENTS);
		double[] updateValues = (double[]) state
				.get(ResilientPropagation.UPDATE_VALUES);

		EngineArray.arrayCopy(lastGradient, ((TrainFlatNetworkResilient) this
				.getFlatTraining()).getLastGradient());
		EngineArray.arrayCopy(updateValues, ((TrainFlatNetworkResilient) this
				.getFlatTraining()).getUpdateValues());

	}

}
