package org.encog.neural.networks.training.propagation.sgd;

/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.training.BatchSize;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

public class StochasticGradientDescent extends BasicTraining implements Train, BatchSize {

	private ContainsFlat network;
	private double learnRate;
	private double momentum;
	private int batchSize;
	
	
	/**
	 * 
	 * @param network
	 *            The network that is to be trained
	 * @param training
	 *            The training set
	 * @param theMiniBatchSize
	 *            The mini-batch size.
	 * @param theLearnRate
	 *            The rate at which the weight matrix will be adjusted based on
	 *            learning.
	 * @param theMomentum
	 *            The influence that previous iteration's training deltas will
	 *            have on the current iteration.
	 */
	public StochasticGradientDescent(final ContainsFlat network,
			final MLDataSet training, final int theMiniBatchSize, 
			final double theLearnRate,
			final double theMomentum) {
		super(TrainingImplementationType.Iterative);
		this.setTraining(training);
		this.network = network;
		this.learnRate = theLearnRate;
		this.momentum = theMomentum;
		this.batchSize = theMiniBatchSize;
	}
	
	@Override
	public void iteration() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canContinue() {
		return false;
	}

	@Override
	public TrainingContinuation pause() {
		return null;
	}

	@Override
	public void resume(TrainingContinuation state) {
		
	}

	@Override
	public MLMethod getMethod() {
		return this.network;
	}

	@Override
	public int getBatchSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setBatchSize(int theBatchSize) {
		// TODO Auto-generated method stub
		
	}
}