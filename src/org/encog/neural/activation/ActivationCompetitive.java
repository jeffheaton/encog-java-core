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

import org.encog.neural.NeuralNetworkError;
import org.encog.persist.Persistor;

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
	 * How many winning neurons are allowed.
	 */
	private int maxWinners = 1;

	
	public static final String[] PARAM_NAMES = {
		"maxWinners" };	
	
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
		this.maxWinners = winners;
	}

	/**
	 * Perform the activation function.
	 * 
	 * @param d
	 *            The data to be given to the activation function.
	 */
	public void activationFunction(final double[] d) {
		final boolean[] winners = new boolean[d.length];
		double sumWinners = 0;

		// find the desired number of winners
		for (int i = 0; i < this.maxWinners; i++) {
			double maxFound = Double.NEGATIVE_INFINITY;
			int winner = -1;

			// find one winner
			for (int j = 0; j < d.length; j++) {
				if (!winners[j] && (d[j] > maxFound)) {
					winner = j;
					maxFound = d[j];
				}
			}
			sumWinners += maxFound;
			winners[winner] = true;
		}

		// adjust weights for winners and non-winners
		for (int i = 0; i < d.length; i++) {
			if (winners[i]) {
				d[i] = d[i] / sumWinners;
			} else {
				d[i] = 0.0;
			}
		}
	}

	/**
	 * @return A cloned copy of this object.
	 */
	@Override
	public Object clone() {
		return new ActivationCompetitive(this.maxWinners);
	}

	/**
	 * Implements the activation function. The array is modified according to
	 * the activation function being used. See the class description for more
	 * specific information on this type of activation function.
	 * 
	 * @param d
	 *            The input array to the activation function.
	 */
	public void derivativeFunction(final double[] d) {
		throw new NeuralNetworkError(
				"Can't use the competitive activation function "
						+ "where a derivative is required.");

	}

	/**
	 * @return The maximum number of winners this function supports.
	 */
	public int getMaxWinners() {
		return this.maxWinners;
	}

	/**
	 * @return False, indication that no derivative is available for this
	 *         function.
	 */
	public boolean hasDerivative() {
		return false;
	}
	
	/**
	 * @return The paramater names for this activation function.
	 */
	@Override
	public String[] getParamNames() {
		return PARAM_NAMES;
	}
	
}
