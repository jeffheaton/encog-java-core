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

package org.encog.engine.network.train;

import org.encog.engine.data.EngineDataSet;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.opencl.EncogCLDevice;

public interface TrainFlatNetwork {

	/**
	 * @return The error from the neural network.
	 */
	double getError();

	/**
	 * @return The trained neural network.
	 */
	FlatNetwork getNetwork();

	/**
	 * @return The data we are training with.
	 */
	EngineDataSet getTraining();

	/**
	 * Perform one training iteration.
	 */
	void iteration();
	/**
	 * Set the number of threads to use.
	 * 
	 * @param numThreads
	 *            The number of threads to use.
	 */
	void setNumThreads(final int numThreads);

	/**
	 * @return The number of threads.
	 */
	int getNumThreads();

	/**
	 * Training is to stop, free any resources.
	 */
	void finishTraining();
}
