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

/**
 * The Linear layer is really not an activation function at all. The input is
 * simply passed on, unmodified, to the output. This activation function is
 * primarily theoretical and of little actual use. Usually an activation
 * function that scales between 0 and 1 or -1 and 1 should be used.
 */
public class ActivationLinear extends BasicActivationFunction {

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = -5356580554235104944L;

	/**
	 * Construct a linear activation function, with a slope of 1.
	 */
	public ActivationLinear() {
		this.params = new double[1];
		this.params[ActivationFunctions.PARAM_LINEAR_SLOPE] = 1;
	}

	/**
	 * @return The object cloned.
	 */
	@Override
	public Object clone() {
		return new ActivationLinear();
	}

	/**
	 * @return Return true, linear has a 1 derivative.
	 */
	public boolean hasDerivative() {
		return true;
	}

	/**
	 * @return The slope of the activation function.
	 */
	public double getSlope() {
		return this.params[ActivationFunctions.PARAM_LINEAR_SLOPE];
	}

	/**
	 * @return The Encog Engine ID for this activation type, or -1 if not
	 *         defined by the Encog engine.
	 */
	@Override
	public int getEngineID() {
		// TODO Auto-generated method stub
		return ActivationFunctions.ACTIVATION_LINEAR;
	}
}
