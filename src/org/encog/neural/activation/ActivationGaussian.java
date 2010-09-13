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
 * An activation function based on the gaussian function.
 * 
 * @author jheaton
 * 
 */
public class ActivationGaussian extends BasicActivationFunction {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -7166136514935838114L;

	/**
	 * Create a gaussian activation function.
	 * 
	 * @param center
	 *            The center of the curve.
	 * @param peak
	 *            The peak of the curve.
	 * @param width
	 *            The width of the curve.
	 */
	public ActivationGaussian(final double center, final double peak,
			final double width) {
		this.params = new double[3];
		this.params[ActivationFunctions.PARAM_GAUSSIAN_CENTER] = center;
		this.params[ActivationFunctions.PARAM_GAUSSIAN_PEAK] = peak;
		this.params[ActivationFunctions.PARAM_GAUSSIAN_WIDTH] = width;
	}

	/**
	 * @return The object cloned.
	 */
	@Override
	public Object clone() {
		return new ActivationGaussian(this.getCenter(), this.getPeak(), this
				.getWidth());
	}

	/**
	 * @return The width of the function.
	 */
	private double getWidth() {
		return this.getParams()[ActivationFunctions.PARAM_GAUSSIAN_WIDTH];
	}

	/**
	 * @return The center of the function.
	 */
	private double getCenter() {
		return this.getParams()[ActivationFunctions.PARAM_GAUSSIAN_CENTER];
	}

	/**
	 * @return The peak of the function.
	 */
	private double getPeak() {
		return this.getParams()[ActivationFunctions.PARAM_GAUSSIAN_PEAK];
	}

	/**
	 * @return Return true, gaussian has a derivative.
	 */
	public boolean hasDerivative() {
		return true;
	}

	/**
	 * @return The Encog Engine ID for this activation type, or -1 if not
	 *         defined by the Encog engine.
	 */
	@Override
	public int getEngineID() {
		// TODO Auto-generated method stub
		return ActivationFunctions.ACTIVATION_GAUSSIAN;
	}

}
