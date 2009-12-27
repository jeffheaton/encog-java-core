/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
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
package org.encog.util.math.rbf;

public class GaussianFunctionMulti implements RadialBasisFunctionMulti {

	/**
	 * The center of the RBF.
	 */
	private final double[] center;
	
	/**
	 * The peak of the RBF.
	 */
	private final double peak;

	/**
	 * The width of the RBF.
	 */
	private final double[] width;
	
	public GaussianFunctionMulti(double peak, double[] center,double[] width)
	{
		this.center = center;
		this.peak = peak;
		this.width = width;
	}
	
	public GaussianFunctionMulti(int dimensions, double peak, double center,double width)
	{
		this.peak = peak;
		this.center = new double[dimensions];		
		this.width = new double[dimensions];
		for(int i=0;i<dimensions;i++)
		{
			this.center[i] = center;
			this.width[i] = width;
		}
	}
	
	public double calculate(double[] x) {
		double value = 0;
		
		for(int i=0;i<center.length;i++) {
			value+=Math.pow(x[i] - this.center[i], 2)
			/ (2.0 * this.width[i] * this.width[i]);
		}		
		return this.peak * Math.exp(-value);
	}

	public double getCenter(int dimension) {
		return this.center[dimension];
	}

	public int getDimensions() {
		return this.center.length;
	}

	public double getPeak() {
		return this.peak;
	}

	public double getWidth(int dimension) {
		return this.width[dimension];
	}

	public void setWidth(double w) {
		for(int i=0;i<width.length;i++) {
			this.width[i] = w;
		}
		
	}

}
