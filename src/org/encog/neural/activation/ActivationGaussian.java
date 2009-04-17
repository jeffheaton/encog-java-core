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
import org.encog.persist.persistors.ActivationGaussianPersistor;
import org.encog.util.math.rbf.GaussianFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An activation function based on the gaussian function.  
 * @author jheaton
 *
 */
public class ActivationGaussian extends BasicActivationFunction {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -7166136514935838114L;

	private GaussianFunction gausian;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public ActivationGaussian(double center,double peak, double width)
	{
		this.gausian = new GaussianFunction(center,peak,width);
	}
	
	public void activationFunction(double[] d) {
		for(int i=0;i<d.length;i++)
		{
			d[i] = gausian.calculate(d[i]);
		}
			
		
	}

	public void derivativeFunction(double[] d) {
		for(int i=0;i<d.length;i++)
		{
			d[i] = gausian.calculateDerivative(d[i]);
		}
		
	}
	
	public Object clone()
	{
		return new ActivationGaussian(this.gausian.getCenter(),
				this.gausian.getPeak(),
				this.gausian.getWidth());
	}
	
	public Persistor createPersistor() {
		return new ActivationGaussianPersistor();
	}

	public GaussianFunction getGausian() {
		return gausian;
	}
	
	

}
