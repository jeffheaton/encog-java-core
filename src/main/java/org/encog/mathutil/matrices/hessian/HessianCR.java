/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.mathutil.matrices.hessian;

import org.encog.mathutil.IntRange;
import org.encog.mathutil.matrices.Matrix;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.GradientWorker;
import org.encog.util.concurrency.DetermineWorkload;
import org.encog.util.concurrency.EngineConcurrency;
import org.encog.util.concurrency.MultiThreadable;
import org.encog.util.concurrency.TaskGroup;

/**
 * Calculate the Hessian matrix using the chain rule method. 
 * 
 */
public class HessianCR extends BasicHessian implements MultiThreadable {
	
	/**
	 * The number of threads to use.
	 */
	private int numThreads;
	
	/**
	 * The workers.
	 */
	private ChainRuleWorker[] workers;
	
	
	/**
	 * {@inheritDoc}
	 */
	public void init(BasicNetwork theNetwork, MLDataSet theTraining) {
		
		super.init(theNetwork,theTraining);
		int weightCount = theNetwork.getStructure().getFlat().getWeights().length;
		
		this.training = theTraining;
		this.network = theNetwork;
		
		this.hessianMatrix = new Matrix(weightCount,weightCount);
		this.hessian = this.hessianMatrix.getData();
		
		// create worker(s)
		final DetermineWorkload determine = new DetermineWorkload(
				this.numThreads, (int) this.training.getRecordCount());

		this.workers = new ChainRuleWorker[determine.getThreadCount()];

		int index = 0;

		// handle CPU
		for (final IntRange r : determine.calculateWorkers()) {
			this.workers[index++] = new ChainRuleWorker(this.flat.clone(),
					this.training.openAdditional(), r.getLow(),
					r.getHigh());
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void compute() {		
		clear();
		double e = 0;
		int weightCount = this.network.getFlat().getWeights().length;
		
		for (int outputNeuron = 0; outputNeuron < this.network.getOutputCount(); outputNeuron++) {
		
			// handle context
			if (this.flat.getHasContext()) {
				this.workers[0].getNetwork().clearContext();
			}

			if (this.workers.length > 1) {

				final TaskGroup group = EngineConcurrency.getInstance()
						.createTaskGroup();

				for (final ChainRuleWorker worker : this.workers) {
					worker.setOutputNeuron(outputNeuron);
					EngineConcurrency.getInstance().processTask(worker, group);
				}

				group.waitForComplete();
			} else {
				this.workers[0].setOutputNeuron(outputNeuron);
				this.workers[0].run();
			}
			
			// aggregate workers

			for (final ChainRuleWorker worker : this.workers) {
				e+=worker.getError();
				for(int i=0;i<weightCount;i++) {
					this.gradients[i] += worker.getGradients()[i];
				}
				updateHessian(worker.getDerivative());
			}
		}
		
		sse= e/2;
	}
	
	/**
	 * Set the number of threads. Specify zero to tell Encog to automatically
	 * determine the best number of threads for the processor. If OpenCL is used
	 * as the target device, then this value is not used.
	 * 
	 * @param numThreads
	 *            The number of threads.
	 */
	@Override
	public final void setThreadCount(final int numThreads) {
		this.numThreads = numThreads;
	}
	
	/**
	 * @return The thread count.
	 */
	@Override
	public int getThreadCount() {
		return this.numThreads;
	}
}
