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
package org.encog.ml;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

/**
 * Defines a training method for a machine learning method.  Most MLMethod 
 * objects need to be trained in some way before they are ready for use.
 *
 */
public interface MLTrain {
	
	/**
	 * @return The training implementation type.
	 */
	TrainingImplementationType getImplementationType();
	
	/**
	 * @return True if training can progress no further.
	 */
	boolean isTrainingDone();
	
	/**
	 * @return The training data to use.
	 */
	NeuralDataSet getTraining();

	/**
	 * Perform one iteration of training.
	 */
	void iteration();
	
	/**
	 * Get the current error percent from the training.
	 * 
	 * @return The current error.
	 */
	double getError();

	/**
	 * Should be called once training is complete and no more iterations are
	 * needed. Calling iteration again will simply begin the training again, and
	 * require finishTraining to be called once the new training session is
	 * complete.
	 * 
	 * It is particularly important to call finishTraining for multithreaded
	 * training techniques.
	 */
	void finishTraining();
	
	/**
	 * Perform a number of training iterations.
	 * @param count The number of iterations to perform.
	 */
	void iteration(int count);
	
	/**
	 * @return The current training iteration.
	 */
	int getIteration();
	
	/**
	 * @return True if the training can be paused, and later continued.
	 */
	boolean canContinue();
	
	/**
	 * Pause the training to continue later.
	 * 
	 * @return A training continuation object.
	 */
	TrainingContinuation pause();

	/**
	 * Resume training.
	 * 
	 * @param state
	 *            The training continuation object to use to continue.
	 */
	void resume(final TrainingContinuation state);




}
