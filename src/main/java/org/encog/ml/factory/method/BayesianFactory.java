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
package org.encog.ml.factory.method;

import org.encog.ml.MLMethod;
import org.encog.ml.bayesian.BayesianNetwork;

/**
 * Factory to create bayesian networks.
 *
 */
public class BayesianFactory {
	
	/**
	 * Create a bayesian network.
	 * @param architecture The architecture to use.
	 * @param input The input neuron count.
	 * @param output The output neuron count.
	 * @return The new bayesian network.
	 */
	public final MLMethod create(final String architecture, final int input,
			final int output) {
		BayesianNetwork method = new BayesianNetwork();
		method.setContents(architecture);
		return method;
	}
}
