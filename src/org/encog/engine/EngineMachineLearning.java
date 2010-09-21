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

package org.encog.engine;

/**
 * Generic interface to a Machine Learning class, such as a neural network or
 * SVM.
 * 
 */
public interface EngineMachineLearning {

	/**
	 * Compute output for the given input.
	 * @param input An array of doubles for the input.
	 * @param output An array of doubles for the output.
	 */
	void compute(double[] input, double[] output);

	/**
	 * @return The input count.
	 */
	int getInputCount();

	/**
	 * @return The output size.
	 */
	int getOutputCount();

}
