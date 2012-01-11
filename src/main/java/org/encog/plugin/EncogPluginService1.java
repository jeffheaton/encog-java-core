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
package org.encog.plugin;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;

/**
 * A service plugin provides services, such as the creation of activation 
 * functions, machine learning methods and training methods.
 *
 */
public interface EncogPluginService1 extends EncogPluginBase {
	
	/**
	 * Create an activation function.
	 * @param name The name of the activation function.
	 * @return The newly created activation function.
	 */
	ActivationFunction createActivationFunction(String name);
	
	/**
	 * Create a new machine learning method.
	 * @param methodType The method to create.
	 * @param architecture The architecture string.
	 * @param input The input count.
	 * @param output The output count.
	 * @return The newly created machine learning method.
	 */
	MLMethod createMethod(final String methodType, 
			final String architecture,
			final int input, final int output);
	
	/**
	 * Create a trainer.
	 * @param method The method to train.
	 * @param training The training data.
	 * @param type Type type of trainer.
	 * @param args The training args.
	 * @return The new training method.
	 */
	MLTrain createTraining(final MLMethod method, 
			final MLDataSet training,
			final String type, final String args);
	
}
