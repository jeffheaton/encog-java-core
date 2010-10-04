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

package org.encog.neural.networks.training.propagation;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.network.train.TrainFlatNetwork;
import org.encog.engine.network.train.prop.OpenCLTrainingProfile;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.FlatUpdateNeeded;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.TrainingError;
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
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
		return this.flatTraining.getNumThreads();
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

			this.flatTraining.iteration();
			this.setError(this.flatTraining.getError());
			this.network.getStructure().setFlatUpdate(FlatUpdateNeeded.Unflatten);

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
	 * Resume training.
	 * 
	 * @param state
	 *            The training continuation object to use to continue.
	 */
	public void resume(final TrainingContinuation state) {
		throw new TrainingError("This training type does not support resume.");
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
		this.flatTraining.setNumThreads(numThreads);
	}
	
	
	/**
	 * Should be called after training has completed and the iteration method
	 * will not be called any further.
	 */
	public void finishTraining() {
		super.finishTraining();
		this.network.getStructure().updateFlatNetwork();
		this.flatTraining.finishTraining();
	}

	/**
	 * @return The OpenCL device to use, or null for the CPU.
	 */
	public OpenCLTrainingProfile getProfile() {
		return null;
	}

	/**
	 * @param flatTraining the flatTraining to set
	 */
	public void setFlatTraining(TrainFlatNetwork flatTraining) {
		this.flatTraining = flatTraining;
	}

	
}
