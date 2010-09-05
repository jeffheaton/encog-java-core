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

package org.encog.neural.networks.training.propagation;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.network.train.TrainFlatNetwork;
import org.encog.engine.network.train.TrainFlatNetworkBackPropagation;
import org.encog.engine.network.train.TrainFlatNetworkManhattan;
import org.encog.engine.network.train.TrainFlatNetworkResilient;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.engine.util.EngineArray;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.FlatUpdateNeeded;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.structure.ValidateForFlat;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.gradient.CalculateGradient;
import org.encog.neural.networks.training.propagation.manhattan.ManhattanPropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.EncogValidate;
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
public abstract class Propagation extends BasicTraining {

	/**
	 * The number of threads to use.
	 */
	private int numThreads = 0;

	/**
	 * The network.
	 */
	private final BasicNetwork network;

	/**
	 * The current flat network we are using for training, or null for none.
	 */
	private FlatNetwork currentFlatNetwork;

	/**
	 * The current flat trainer we are using, or null for none.
	 */
	private TrainFlatNetwork flatTraining;

	/**
	 * Should we attempt to flatten the network? Usually you want this to be
	 * true, a flat network can train much faster. However, we can not always
	 * use flat networks. See the FlatNetwork class for more info.
	 */
	private boolean attemptFlatten;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private EncogCLDevice targetDevice;

	/**
	 * Construct a propagation object.
	 * 
	 * @param network
	 *            The network.
	 * @param training
	 *            The training set.
	 */
	public Propagation(final BasicNetwork network, final NeuralDataSet training) {
		super();
		this.network = network;
		setTraining(training);
		this.attemptFlatten = true;
	}

	/**
	 * @return True if this training can be continued.
	 */
	public boolean canContinue() {
		return false;
	}

	/**
	 * @return the currentFlatNetwork
	 */
	public FlatNetwork getCurrentFlatNetwork() {
		return this.currentFlatNetwork;
	}

	/**
	 * @return the flatTraining
	 */
	public TrainFlatNetwork getFlatTraining() {
		return this.flatTraining;
	}

	/**
	 * @return The network.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * @return The number of threads.
	 */
	public int getNumThreads() {
		return this.numThreads;
	}

	/**
	 * @return the attemptFlatten
	 */
	public boolean isAttemptFlatten() {
		return this.attemptFlatten;
	}

	/**
	 * Determine if this specified training continuation object is valid for
	 * this training method.
	 * 
	 * @param state
	 *            The training continuation object to check.
	 * @return True if the continuation object is valid.
	 */
	public boolean isValidResume(final TrainingContinuation state) {
		return false;
	}

	/**
	 * Perform one training iteration.
	 */
	public void iteration() {
		try {
			preIteration();

			if (this.flatTraining == null && this.attemptFlatten==true )
				attemptFlatten();
			
			if (this.flatTraining == null) {
				final CalculateGradient prop = new CalculateGradient(
						this.network, getTraining(), getNumThreads());
				final double[] weights = NetworkCODEC
						.networkToArray(this.network);
				prop.calculate(weights);

				performIteration(prop, weights);

				NetworkCODEC.arrayToNetwork(weights, this.network);
				setError(prop.getError());
			} else {
				this.flatTraining.iteration();
				this.setError(this.flatTraining.getError());
				this.network.getStructure().setFlatUpdate(FlatUpdateNeeded.Unflatten);
			}

			postIteration();
			
			if( this.logger.isInfoEnabled() ) {
				logger.info("Training iteration done, error: " + this.getError());
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			EncogValidate.validateNetworkForTraining(this.network,
					getTraining());
			throw new EncogError(ex);
		}
	}
	
	

	/**
	 * Pause the training to continue later.
	 * 
	 * @return A training continuation object.
	 */
	public TrainingContinuation pause() {
		throw new TrainingError("This training type does not support pause.");
	}

	/**
	 * Perform an iteration. This is implemented for each of the propagation
	 * method types.
	 * 
	 * @param prop
	 *            The gradients.
	 * @param weights
	 *            The weights.
	 */
	public abstract void performIteration(CalculateGradient prop,
			double[] weights);


	/**
	 * Resume training.
	 * 
	 * @param state
	 *            The training continuation object to use to continue.
	 */
	public void resume(final TrainingContinuation state) {
		throw new TrainingError("This training type does not support resume.");
	}

	/**
	 * @param attemptFlatten
	 *            the attemptFlatten to set
	 */
	public void setAttemptFlatten(final boolean attemptFlatten) {
		this.attemptFlatten = attemptFlatten;
	}

	/**
	 * Set the number of threads. Specify zero to tell Encog to automatically
	 * determine the best number of threads for the processor. If OpenCL is used
	 * as the target device, then this value is not used.
	 * 
	 * @param numThreads
	 *            The number of threads.
	 */
	public void setNumThreads(final int numThreads) {
		this.numThreads = numThreads;
	}
	
	public void attemptFlatten()
	{
		ValidateForFlat val = new ValidateForFlat();
		if( val.isValid(this.network)==null )
		{
			this.network.getStructure().updateFlatNetwork();
			
			if( this instanceof Backpropagation )
			{
				Backpropagation back = (Backpropagation)this;
				TrainFlatNetworkBackPropagation backFlat = new TrainFlatNetworkBackPropagation(
						this.network.getStructure().getFlat(),
						this.getTraining(),
						back.getLearningRate(),
						back.getMomentum());
				this.flatTraining = backFlat;
				EngineArray.arrayCopy(back.getLastDelta(),backFlat.getLastDelta());
				this.flatTraining.setTargetDevice(this.targetDevice);
			}
			else if( this instanceof ResilientPropagation )
			{
				ResilientPropagation rprop = (ResilientPropagation)this;
				TrainFlatNetworkResilient rpropFlat = new TrainFlatNetworkResilient(
						this.network.getStructure().getFlat(),
						this.getTraining()); 
				this.flatTraining = rpropFlat;
				
				EngineArray.arrayCopy(rprop.getLastGradient(),rpropFlat.getLastGradient());
				EngineArray.arrayCopy(rprop.getUpdateValues(),rpropFlat.getUpdateValues());
				this.flatTraining.setTargetDevice(this.targetDevice);
			}
			else if( this instanceof ManhattanPropagation )
			{
				this.flatTraining = new TrainFlatNetworkManhattan(
						this.network.getStructure().getFlat(),
						this.getTraining(),
						((ManhattanPropagation)this).getLearningRate());
				this.flatTraining.setTargetDevice(this.targetDevice);
			}
		}
		else
			this.attemptFlatten = false;
	}
	
	/**
	 * Should be called after training has completed and the iteration method
	 * will not be called any further.
	 */
	public void finishTraining() {
		super.finishTraining();
		this.network.getStructure().updateFlatNetwork();
	}

	/**
	 * @return The OpenCL device to use, or null for the CPU.
	 */
	public EncogCLDevice getTargetDevice() {
		return targetDevice;
	}

	/**
	 * Sets the target device for the training to run on. Specify null for the
	 * CPU, or some other OpenCL device. Must be set before the first training
	 * iteration.
	 * 
	 * @param targetDevice
	 *            The OpenCL device to use, or null to use the CPU.
	 */
	public void setTargetDevice(EncogCLDevice targetDevice) {
		this.targetDevice = targetDevice;
	}
	
	/**
	 * Assign the CPU to this trainer.
	 */
	public void assignCPU()
	{
		this.targetDevice = null;
	}
	
	/**
	 * Assigns the first available OpenCL device. If you only have one GPU, this
	 * method will probably work just fine. However, if you are dealing with
	 * multiple OpenCL devices you should directly select the desired OpenCL
	 * device.
	 */
	public void assignOpenCL()
	{
		this.targetDevice = Encog.getInstance().getCL().getEnabledDevices().get(0);
	}
}
