/*
 * Encog(tm) Core v2.6 - Java Version
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
package org.encog.engine.network.activation;

import java.io.Serializable;

/**
 * This interface allows various activation functions to be used with the neural
 * network. Activation functions are applied to the output from each layer of a
 * neural network. Activation functions scale the output into the desired range.
 * 
 * Methods are provided both to process the activation function, as well as the
 * derivative of the function. Some training algorithms, particularly back
 * propagation, require that it be possible to take the derivative of the
 * activation function.
 * 
 * Not all activation functions support derivatives. If you implement an
 * activation function that is not derivable then an exception should be thrown
 * inside of the derivativeFunction method implementation.
 * 
 * Non-derivable activation functions are perfectly valid, they simply cannot be
 * used with every training algorithm.
 */
public interface ActivationFunction extends Serializable, Cloneable {

	/**
	 * Implements the activation function. The array is modified according to
	 * the activation function being used. See the class description for more
	 * specific information on this type of activation function.
	 * 
	 * @param d
	 *            The input array to the activation function.
	 * @param start
	 * 		The starting index.
	 * @param size
	 * 		The number of values to calculate.
	 */
	void activationFunction(double[] d, int start, int size);

	/**
	 * Calculate the derivative of the activation. It is assumed that the value
	 * d, which is passed to this method, was the output from this activation.
	 * This prevents this method from having to recalculate the activation, just
	 * to recalculate the derivative.
	 * 
	 * The array is modified according derivative of the activation function
	 * being used. See the class description for more specific information on
	 * this type of activation function. Propagation training requires the
	 * derivative. Some activation functions do not support a derivative and
	 * will throw an error.
	 * 
	 * @param d
	 *            The input array to the activation function.
	 * @return The derivative.
	 */
	double derivativeFunction(double d);

	/**
	 * @return Return true if this function has a derivative.
	 */
	boolean hasDerivative();

	/**
	 * @return The params for this activation function.
	 */
	double[] getParams();

	/**
	 * Set one of the params for this activation function.
	 * @param index The index of the param to set.
	 * @param value The value to set.
	 */
	void setParam(int index, double value);

	/**
	 * @return The names of the parameters.
	 */
	String[] getParamNames();

	/**
	 * @return A cloned copy of this activation function.
	 */
	ActivationFunction clone();
	
	/**
	 * Returns the OpenCL expression for this activation function.
	 * @param derivative True if we want the derivative, false otherwise.
	 * @return The OpenCL expression for this activation function.
	 */
	String getOpenCLExpression(final boolean derivative);
}
