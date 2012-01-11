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
package org.encog.neural.networks.training.concurrent.performers;

import org.encog.neural.networks.training.concurrent.ConcurrentTrainingManager;
import org.encog.neural.networks.training.concurrent.jobs.TrainingJob;

/**
 * Performers actually perform the training. Currently there are performers for
 * OpenCL and CPU.
 * 
 */
public interface ConcurrentTrainingPerformer extends Runnable {
	
	/**
	 * @return True, if this performer is ready to train a job.
	 */
	boolean ready();

	/**
	 * Perform the specified job.
	 * @param job The job to perform.
	 */
	void perform(TrainingJob job);
	
	/**
	 * Set the manager.
	 * @param manager The manager.
	 */
	void setManager(ConcurrentTrainingManager manager);
	
	/**
	 * Get the manager.
	 * @return The manager
	 */
	ConcurrentTrainingManager getManager();
}
