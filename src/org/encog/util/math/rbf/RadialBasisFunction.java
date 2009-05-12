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

package org.encog.util.math.rbf;

/**
 * Provides a generic interface to a radial basis function (RBF). Encog uses
 * RBF's for a variety of purposes.
 * 
 * @author jheaton
 * 
 */
public interface RadialBasisFunction {

	/**
	 * Calculate the RBF result for the specified value.
	 * @param x The value to be passed into the RBF.
	 * @return The RBF value.
	 */
	double calculate(double x);

	/**
	 * Calculate the derivative of the RBF function.
	 * @param x The value to calculate for.
	 * @return The calculated value.
	 */
	double calculateDerivative(double x);

	/**
	 * @return The center of the RBF.
	 */
	double getCenter();

	/**
	 * @return The peak of the RBF.
	 */
	double getPeak();

	/**
	 * @return The width of the RBF.
	 */
	double getWidth();
}
