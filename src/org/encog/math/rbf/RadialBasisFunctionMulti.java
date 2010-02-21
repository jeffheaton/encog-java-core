/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
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

package org.encog.math.rbf;

/**
 * A multi-dimension RBF.
 */
public interface RadialBasisFunctionMulti {
	/**
	 * Calculate the RBF result for the specified value.
	 * 
	 * @param x
	 *            The value to be passed into the RBF.
	 * @return The RBF value.
	 */
	double calculate(double[] x);

	/**
	 * Get the center of this RBD.
	 * @param dimension The dimension to get the center for.
	 * @return The center of the RBF.
	 */
	double getCenter(int dimension);

	/**
	 * Get the center of this RBD.
	 * @return The center of the RBF.
	 */
	double getPeak();

	/**
	 * Get the center of this RBD.
	 * @param dimension The dimension to get the center for.
	 * @return The center of the RBF.
	 */
	double getWidth(int dimension);
	
	/**
	 * @return The dimensions in this RBF.
	 */
	int getDimensions();

	/**
	 * Set the width.
	 * @param radius The width.
	 */
	void setWidth(double radius);	
}
