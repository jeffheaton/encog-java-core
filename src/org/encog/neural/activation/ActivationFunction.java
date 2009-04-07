/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.neural.activation;

import java.io.Serializable;

import org.encog.neural.persist.EncogPersistedObject;


/**
 * ActivationFunction: This interface allows various activation functions to be
 * used with the neural network. Activation functions are applied to
 * the output from each layer of a neural network. Activation functions scale
 * the output into the desired range.
 * 
 * Methods are provided both to process the activation function, as well as
 * the derivative of the function.  Some training algorithms, particularly
 * back propagation, require that it be possible to take the derivative
 * of the activation function.
 * 
 * Not all activation functions support derivatives.  If you implement an 
 * activation function that is not derivable then an exception should be thrown
 * inside of the derivativeFunction method implementation.
 * 
 * Non-derivable activation functions are perfectly valid, they simply cannot be
 * used with every training algorithm. 
 */
public interface ActivationFunction extends EncogPersistedObject {

	/**
	 * A activation function for a neural network.
	 * 
	 * @param d
	 *            The input to the function.
	 * @return The output from the function.
	 */
	void activationFunction(double[] d);

	/**
	 * Performs the derivative of the activation function function on the input.
	 * 
	 * @param d
	 *            The input.
	 * @return The output.
	 */
	void derivativeFunction(double[] d);
	
}
