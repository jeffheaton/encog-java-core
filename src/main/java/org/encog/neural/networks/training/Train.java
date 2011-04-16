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
package org.encog.neural.networks.training;

import java.util.List;

import org.encog.ml.MLMethod;
import org.encog.ml.MLTrain;

/**
 * Interface for all neural network training methods. This allows the training
 * methods to be largely interchangeable. Though some training methods require
 * specific types of neural network structure.
 */

public interface Train extends MLTrain {

	/**
	 * Training strategies can be added to improve the training results. There
	 * are a number to choose from, and several can be used at once.
	 * 
	 * @param strategy
	 *            The strategy to add.
	 */
	void addStrategy(Strategy strategy);

	/**
	 * Get the current best network from the training.
	 * 
	 * @return The best network.
	 */
	MLMethod getNetwork();

	/**
	 * @return The strategies to use.
	 */
	List<Strategy> getStrategies();
	

	/**
	 * @param error
	 *            Set the current error rate. This is usually used by training
	 *            strategies.
	 */
	void setError(double error);
			
	/**
	 * Set the current training iteration.
	 * @param iteration Iteration.
	 */
	void setIteration(int iteration);
}
