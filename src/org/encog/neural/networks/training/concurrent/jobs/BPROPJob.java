/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.back.Backpropagation;

/**
 * A training definition for BPROP training.
 */
public class BPROPJob extends TrainingJob {

	/**
	 * The learning rate to use.
	 */
	private double learningRate;

	/**
	 * The momentum to use.
	 */
	private double momentum;

	/**
	 * Construct a job definition for RPROP. For more information on backprop,
	 * see the Backpropagation class.  Use OpenCLratio of 1.0 and process one
	 * iteration per cycle.
	 * 
	 * @param network
	 *            The network to use.
	 * @param training
	 *            The training data to use.
	 * @param loadToMemory
	 *            Should binary data be loaded to memory?
	 * @param learningRate
	 *            THe learning rate to use.
	 * @param momentum
	 *            The momentum to use.
	 */
	public BPROPJob(final BasicNetwork network, final NeuralDataSet training,
			final boolean loadToMemory, final double learningRate,
			final double momentum) {
		this(network,training,loadToMemory,learningRate,momentum,1.0,1,1.0,1);
	}
	
	/**
	 * Construct a job definition for RPROP. For more information on backprop,
	 * see the Backpropagation class.
	 * 
	 * @param network
	 *            The network to use.
	 * @param training
	 *            The training data to use.
	 * @param loadToMemory
	 *            Should binary data be loaded to memory?
	 * @param learningRate
	 *            THe learning rate to use.
	 * @param momentum
	 *            The momentum to use.
	 * @param localRatio
	 * 		The local ratio, used if this job is performed by an OpenCL Device.
	 * @param globalRatio
	 * 		The global ratio, used if this job is performed by an OpenCL Device.
	 * @param segmentationRatio
	 * 		The segmentation ratio, used if this job is performed by an OpenCL Device.
	 * @param iterationsPer
	 * 			How many iterations to process per cycle.
	 */
	public BPROPJob(final BasicNetwork network, final NeuralDataSet training,
			final boolean loadToMemory, final double learningRate,
			final double momentum, final double localRatio, final int globalRatio, final double segmentationRatio, final int iterationsPer) {
		super(network, training, loadToMemory);
		this.learningRate = learningRate;
		this.momentum = momentum;
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
		final Propagation train = new Backpropagation(getNetwork(), getTraining(),
				profile, getLearningRate(), getMomentum());
		
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
	 * @return the learningRate
	 */
	public double getLearningRate() {
		return this.learningRate;
	}

	/**
	 * @return the momentum
	 */
	public double getMomentum() {
		return this.momentum;
	}

	/**
	 * @param learningRate
	 *            the learningRate to set
	 */
	public void setLearningRate(final double learningRate) {
		this.learningRate = learningRate;
	}

	/**
	 * @param momentum
	 *            the momentum to set
	 */
	public void setMomentum(final double momentum) {
		this.momentum = momentum;
	}

}
