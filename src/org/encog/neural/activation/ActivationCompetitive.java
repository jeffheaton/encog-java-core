/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.neural.activation;

import org.encog.engine.network.flat.ActivationFunctions;
import org.encog.neural.NeuralNetworkError;

/**
 * An activation function that only allows a specified number, usually one, of
 * the out-bound connection to win. These connections will share in the sum of
 * the output, whereas the other neurons will receive zero.
 * 
 * This activation function can be useful for "winner take all" layers.
 * 
 */
public class ActivationCompetitive extends BasicActivationFunction {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 5396927873082336888L;

	/**
	 * Create a competitive activation function with one winner allowed.
	 */
	public ActivationCompetitive() {
		this(1);
	}

	/**
	 * Create a competitive activation function with the specified maximum
	 * number of winners.
	 * 
	 * @param winners
	 *            The maximum number of winners that this function supports.
	 */
	public ActivationCompetitive(final int winners) {
		this.params = new double[1];
		this.params[ActivationFunctions.PARAM_COMPETITIVE_MAX_WINNERS] = winners;
	}

	/**
	 * @return A cloned copy of this object.
	 */
	@Override
	public Object clone() {
		return new ActivationCompetitive(
				(int) this.params[ActivationFunctions.PARAM_COMPETITIVE_MAX_WINNERS]);
	}

	/**
	 * Implements the activation function. The array is modified according to
	 * the activation function being used. See the class description for more
	 * specific information on this type of activation function.
	 * 
	 * @param d
	 *            The input array to the activation function.
	 * @return The derivative.
	 */
	public double derivativeFunction(final double d) {
		throw new NeuralNetworkError(
				"Can't use the competitive activation function "
						+ "where a derivative is required.");

	}

	/**
	 * @return The maximum number of winners this function supports.
	 */
	public int getMaxWinners() {
		return (int) this.params[ActivationFunctions.PARAM_COMPETITIVE_MAX_WINNERS];
	}

	/**
	 * @return False, indication that no derivative is available for this
	 *         function.
	 */
	public boolean hasDerivative() {
		return false;
	}

	/**
	 * @return The Encog Engine ID for this activation type, or -1 if not
	 *         defined by the Encog engine.
	 */
	@Override
	public int getEngineID() {
		return ActivationFunctions.ACTIVATION_COMPETITIVE;
	}

}
