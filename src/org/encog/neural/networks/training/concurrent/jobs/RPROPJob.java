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

package org.encog.neural.networks.training.concurrent.jobs;

import org.encog.engine.network.train.prop.OpenCLTrainingProfile;
import org.encog.engine.network.train.prop.RPROPConst;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

/**
 * A training definition for RPROP training.
 */
public class RPROPJob extends TrainingJob {

	/**
	 * The initial update value.
	 */
	private double initialUpdate;

	/**
	 * The maximum step value.
	 */
	private double maxStep;

	/**
	 * Construct an RPROP job. For more information on RPROP see the
	 * ResilientPropagation class.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 * @param loadToMemory
	 *            True if binary training data should be loaded to memory.
	 * @param initialUpdate
	 *            The initial update.
	 * @param maxStep
	 *            The max step.
	 */
	public RPROPJob(final BasicNetwork network, final NeuralDataSet training,
			final boolean loadToMemory) {
		this(network,training,loadToMemory,RPROPConst.DEFAULT_INITIAL_UPDATE,RPROPConst.DEFAULT_MAX_STEP,1,1,1,1);
	}

	/**
	 * Construct an RPROP job. For more information on RPROP see the
	 * ResilientPropagation class.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 * @param loadToMemory
	 *            True if binary training data should be loaded to memory.
	 * @param initialUpdate
	 *            The initial update.
	 * @param maxStep
	 *            The max step.
	 * @param localRatio
	 * 		The local ratio, used if this job is performed by an OpenCL Device.
	 * @param globalRatio
	 * 		The global ratio, used if this job is performed by an OpenCL Device.
	 * @param segmentationRatio
	 * 		The segmentation ratio, used if this job is performed by an OpenCL Device.
	 * @param iterationsPer
	 * 			How many iterations to process per cycle.
	 */
	public RPROPJob(final BasicNetwork network, final NeuralDataSet training,
			final boolean loadToMemory, final double localRatio, final int globalRatio, final double segmentationRatio, final int iterationsPer) {
		
		this(network,training,
			loadToMemory,RPROPConst.DEFAULT_INITIAL_UPDATE,
			RPROPConst.DEFAULT_MAX_STEP,localRatio,globalRatio,segmentationRatio,iterationsPer);
	}
	
	/**
	 * Construct an RPROP job. For more information on RPROP see the
	 * ResilientPropagation class.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 * @param loadToMemory
	 *            True if binary training data should be loaded to memory.
	 * @param initialUpdate
	 *            The initial update.
	 * @param maxStep
	 *            The max step.
	 * @param localRatio
	 * 		The local ratio, used if this job is performed by an OpenCL Device.
	 * @param globalRatio
	 * 		The global ratio, used if this job is performed by an OpenCL Device.
	 * @param segmentationRatio
	 * 		The segmentation ratio, used if this job is performed by an OpenCL Device.
	 * @param iterationsPer
	 * 			How many iterations to process per cycle.
	 */
	public RPROPJob(final BasicNetwork network, final NeuralDataSet training,
			final boolean loadToMemory, final double initialUpdate,
			final double maxStep, final double localRatio, final int globalRatio, final double segmentationRatio, final int iterationsPer) {
		super(network, training, loadToMemory);
		this.initialUpdate = initialUpdate;
		this.maxStep = maxStep;
		this.setLocalRatio(localRatio);
		this.setGlobalRatio(globalRatio);
		this.setSegmentationRatio(segmentationRatio);
		this.setIterationsPer(iterationsPer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createTrainer(final OpenCLTrainingProfile profile, boolean singleThreaded) {
		final Propagation train = new ResilientPropagation(getNetwork(),
				getTraining(), profile, getInitialUpdate(), getMaxStep());

		if( singleThreaded )
			train.setNumThreads(1);
		else
			train.setNumThreads(0);
			
		
		for (final Strategy strategy : getStrategies()) {
			train.addStrategy(strategy);
		}

		setTrain(train);
	}

	/**
	 * @return the initialUpdate
	 */
	public double getInitialUpdate() {
		return this.initialUpdate;
	}

	/**
	 * @return the maxStep
	 */
	public double getMaxStep() {
		return this.maxStep;
	}

	/**
	 * @param initialUpdate
	 *            the initialUpdate to set
	 */
	public void setInitialUpdate(final double initialUpdate) {
		this.initialUpdate = initialUpdate;
	}

	/**
	 * @param maxStep
	 *            the maxStep to set
	 */
	public void setMaxStep(final double maxStep) {
		this.maxStep = maxStep;
	}

}
