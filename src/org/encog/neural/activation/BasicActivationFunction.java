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
import org.encog.persist.EncogCollection;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.generic.GenericPersistor;

/**
 * Holds basic functionality that all activation functions will likely have use
 * of. Specifically it implements a name and description for the
 * EncogPersistedObject class.
 * 
 * @author jheaton
 * 
 */
public abstract class BasicActivationFunction implements ActivationFunction {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 672555213449163812L;
	
	
	protected double[] params;
	
	public BasicActivationFunction()
	{
		this.params = new double[0];
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

		ActivationFunctions.calculateActivation(
				this.getEngineID(), 
				d, 
				this.params,
				0,
				d.length,
				0);
	}

	/**
	 * Calculate the derivative of the activation. It is assumed that the value
	 * d, which is passed to this method, was the output from this activation.
	 * This prevents this method from having to recalculate the activation, just
	 * to recalculate the derivative.
	 * 
	 * Some activation functions do not have derivatives and will throw an error.
	 * 
	 * Linear functions will return one for their derivative.
	 * 
	 * @param d
	 *            The input array to the activation function.
	 *            
	 * @return 	The derivative.
	 */
	public double derivativeFunction(final double d) {
		return ActivationFunctions.calculateActivationDerivative(
				this.getEngineID(), 
				d, 
				this.params,
				0);

	}
	
	/**
	 * @return The object cloned.
	 */
	@Override
	public abstract Object clone();

	
	public double[] getParams()
	{
		return this.params;
	}
	
	public void setParam(int index, double value)
	{
		this.params[index] = value;
	}
	
	public String[] getParamNames()
	{
		return ActivationFunctions.getParams(getEngineID());
	}

}
