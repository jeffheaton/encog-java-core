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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The hyperbolic tangent activation function takes the curved
 * shape of the hyperbolic tangent. This activation function produces both
 * positive and negative output. Use this activation function if both negative
 * and positive output is desired.
 */
public class ActivationTANH extends BasicActivationFunction {

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = 9121998892720207643L;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * A threshold function for a neural network.
	 * 
	 * @param d
	 *            The input to the function.
	 * @return The output from the function.
	 */
	public void activationFunction(final double[] d) {
		
		for(int i=0;i<d.length;i++)
		{
			d[i] = activationFunction(d[i]);
		}

	}
	
	private double activationFunction(double d)
	{
		return (BoundMath.exp(d * 2.0) - 1.0)
		/ (BoundMath.exp(d * 2.0) + 1.0);
	}

	/**
	 * Some training methods require the derivative.
	 * 
	 * @param d
	 *            The input.
	 * @return The output.
	 */
	public void derivativeFunction(final double[] d) {
		
		for(int i=0;i<d.length;i++)
		{
			d[i] = 1.0 - BoundMath.pow(activationFunction(d[i]), 2.0);
		}
	}
	
	public Object clone()
	{
		return new ActivationTANH();
	}
	
	@Override
	public Persistor createPersistor() {
		return new ActivationTANHPersistor();
	}
	
	
}
