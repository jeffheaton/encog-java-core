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
package org.encog.ml.train.strategy;

import org.encog.ml.train.MLTrain;

/**
 * Training strategies can be added to training algorithms.  Training 
 * strategies allow different additional logic to be added to an existing
 * training algorithm.  There are a number of different training strategies
 * that can perform various tasks, such as adjusting the learning rate or 
 * momentum, or terminating training when improvement diminishes.  Other 
 * strategies are provided as well.
 * 
 * @author jheaton
 *
 */
public interface Strategy {
	
	/**
	 * Initialize this strategy.
	 * @param train The training algorithm.
	 */
	void init(MLTrain train);
	
	/**
	 * Called just before a training iteration.
	 */
	void preIteration();
	
	/**
	 * Called just after a training iteration.
	 */
	void postIteration();
	
}
