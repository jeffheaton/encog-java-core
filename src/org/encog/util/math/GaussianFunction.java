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

package org.encog.util.math;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GaussianFunction {
	
	private double center;
	private double peak;
	private double width;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public GaussianFunction(double center,double peak,double width)
	{
		this.center = center;
		this.peak = peak;
		this.width = width;
	}
	
	public double gaussian(double x)
	{
		return (this.peak*MathConst.EULERS_NUMBER)*(-(Math.pow(x-this.center, 2.0)/2.0*Math.pow(this.width, 2.0)));
	}
	
	public double gaussianDerivative(double x) {
		return ((-x)*Math.pow(this.width, -2))+(this.center*Math.pow(this.width, -2));
	}

	public double getCenter() {
		return center;
	}

	public double getPeak() {
		return peak;
	}

	public double getWidth() {
		return width;
	}

}
