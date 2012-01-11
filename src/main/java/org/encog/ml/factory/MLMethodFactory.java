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
package org.encog.ml.factory;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.plugin.EncogPluginBase;
import org.encog.plugin.EncogPluginService1;

/**
 * This factory is used to create machine learning methods.
 */
public class MLMethodFactory {
	
	/**
	 * String constant for a bayesian neural network.
	 */
	public static final String TYPE_BAYESIAN = "bayesian";
	
	/**
	 * String constant for feedforward neural networks.
	 */
	public static final String TYPE_FEEDFORWARD = "feedforward";

	/**
	 * String constant for RBF neural networks.
	 */
	public static final String TYPE_RBFNETWORK = "rbfnetwork";

	/**
	 * String constant for support vector machines.
	 */
	public static final String TYPE_SVM = "svm";

	/**
	 * String constant for SOMs.
	 */
	public static final String TYPE_SOM = "som";

	/**
	 * A probabilistic neural network. Supports both PNN & GRNN.
	 */
	public static final String TYPE_PNN = "pnn";

	/**
	 * Create a new machine learning method.
	 * 
	 * @param methodType
	 *            The method to create.
	 * @param architecture
	 *            The architecture string.
	 * @param input
	 *            The input count.
	 * @param output
	 *            The output count.
	 * @return The newly created machine learning method.
	 */
	public final MLMethod create(final String methodType,
			final String architecture, final int input, final int output) {
		for (EncogPluginBase plugin : Encog.getInstance().getPlugins()) {
			if (plugin instanceof EncogPluginService1) {
				MLMethod result = ((EncogPluginService1) plugin).createMethod(
						methodType, architecture, input, output);
				if (result != null) {
					return result;
				}
			}
		}

		throw new EncogError("Unknown method type: " + methodType);
	}

}
