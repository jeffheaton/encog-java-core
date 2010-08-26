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
import org.encog.mathutil.rbf.GaussianFunction;
import org.encog.persist.Persistor;

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
	 * The gaussian function to be used.
	 */
	private GaussianFunction gausian;

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
		this.gausian = new GaussianFunction(center, peak, width);
	}

	/**
	 * Implements the activation function. The array is modified according to
	 * the activation function being used. See the class description for more
	 * specific information on this type of activation function.
	 * 
	 * @param d
	 *            The input array to the activation function.
	 */
	public void activationFunction(final double[] d) {
		for (int i = 0; i < d.length; i++) {
			d[i] = this.gausian.calculate(d[i]);
		}

	}

	/**
	 * @return The object cloned.
	 */
	@Override
	public Object clone() {
		return new ActivationGaussian(this.gausian.getCenter(), this.gausian
				.getPeak(), this.gausian.getWidth());
	}

	/**
	 * Implements the activation function derivative. The array is modified
	 * according derivative of the activation function being used. See the class
	 * description for more specific information on this type of activation
	 * function. Propagation training requires the derivative. Some activation
	 * functions do not support a derivative and will throw an error.
	 * 
	 * @param d
	 *            The input array to the activation function.
	 */
	public double derivativeFunction(double d) {
			return this.gausian.calculateDerivative(d);


	}

	/**
	 * @return The gaussian funcion used.
	 */
	public GaussianFunction getGausian() {
		return this.gausian;
	}

	/**
	 * @return Return true, gaussian has a derivative.
	 */
	public boolean hasDerivative() {
		return true;
	}
	
	public void setParam(int index, double value)
	{
		super.setParam(index,value);

		this.gausian = new GaussianFunction(
				this.params[ActivationFunctions.PARAM_GAUSSIAN_CENTER],
				this.params[ActivationFunctions.PARAM_GAUSSIAN_PEAK],
				this.params[ActivationFunctions.PARAM_GAUSSIAN_WIDTH]);
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
