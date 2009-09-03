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

import org.encog.persist.Persistor;
import org.encog.persist.persistors.ActivationTANHPersistor;
import org.encog.util.math.BoundMath;

/**
 * The hyperbolic tangent activation function takes the curved shape of the
 * hyperbolic tangent. This activation function produces both positive and
 * negative output. Use this activation function if both negative and positive
 * output is desired.
 * 
 * This implementation does an approximation of the TANH function, using only a
 * single base e exponent.  This has a considerable effect on performance, adds
 * only minimal change to the output compared to a standard TANH calculation.
 */
public class ActivationTANH extends BasicActivationFunction {

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = 9121998892720207643L;

	/**
	 * Internal activation function that performs the TANH.
	 * @param d The input value.
	 * @return The output value.
	 */
	private double activationFunction(final double d) {
		return -1 + (2/ (1+BoundMath.exp(-2* d ) ) );
	}

	/**
	 * Implements the activation function.  The array is modified according
	 * to the activation function being used.  See the class description
	 * for more specific information on this type of activation function.
	 * @param d The input array to the activation function.
	 */
	public void activationFunction(final double[] d) {

		for (int i = 0; i < d.length; i++) {
			d[i] = activationFunction(d[i]);
		}

	}

	/**
	 * @return The object cloned;
	 */
	@Override
	public Object clone() {
		return new ActivationTANH();
	}

	/**
	 * Create a Persistor for this activation function.
	 * @return The persistor.
	 */
	@Override
	public Persistor createPersistor() {
		return new ActivationTANHPersistor();
	}

	/**
	 * Implements the activation function derivative.  The array is modified 
	 * according derivative of the activation function being used.  See the 
	 * class description for more specific information on this type of 
	 * activation function. Propagation training requires the derivative. 
	 * Some activation functions do not support a derivative and will throw
	 * an error.
	 * @param d The input array to the activation function.
	 */
	public void derivativeFunction(final double[] d) {

		for (int i = 0; i < d.length; i++) {
			d[i] = 1.0 - BoundMath.pow(activationFunction(d[i]), 2.0);
		}
	}
	
	/**
	 * @return Return true, TANH has a derivative.
	 */
	public boolean hasDerivative() {
		return true;
	}

}
