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
package org.encog.neural.networks.training;

import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

public class MockTrain extends BasicTraining implements LearningRate, Momentum {

	public MockTrain() {
		super(TrainingImplementationType.Iterative);
	}

	private BasicNetwork network;
	private boolean wasUsed;
	private double momentum;
	private double learningRate;

	
	public MLMethod getMethod() {
		return this.network;
	}
	
	public void setNetwork(BasicNetwork network)
	{
		this.network = network;
	}

	public void simulate(double newError, double firstValue) {
		preIteration();
		MockTrain.setFirstElement(firstValue, this.network);
		setError(newError);
		postIteration();
		this.wasUsed = true;
	}
	
	public void iteration() {
		preIteration();
		postIteration();
		this.wasUsed = true;
	}
	
	public static void setFirstElement(double value, BasicNetwork network)
	{
		double[] d = NetworkCODEC.networkToArray(network);
		d[0] = value;
		NetworkCODEC.arrayToNetwork(d, network);
	}
	
	public static double getFirstElement(BasicNetwork network)
	{
		double[] d = NetworkCODEC.networkToArray(network);
		return d[0];
	}

	public boolean wasUsed() {
		return wasUsed;
	}

	public double getLearningRate() {
		return this.learningRate;
	}

	public void setLearningRate(double rate) {
		this.learningRate = rate;
	}

	public double getMomentum() {
		return this.momentum;
	}

	public void setMomentum(double m) {
		this.momentum = m;
		
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



}
