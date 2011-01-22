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
package org.encog.engine.validate;

import org.encog.engine.EngineMachineLearning;

/**
 * Base class for validation. Validate if a particular network or machine
 * learning class is suitable for a specific purpose.
 * 
 */
public abstract class BasicMachineLearningValidate implements
		ValidateMachineLearning {

	/**
	 * Determine if the network is valid. If invalid, throw an error.
	 * 
	 * @param network
	 *            The network to check.
	 */
	@Override
	public void validate(final EngineMachineLearning network) {
		final String msg = isValid(network);
		if (msg != null) {
			throw new ValidateMachineLearningError(msg);
		}
	}

}
