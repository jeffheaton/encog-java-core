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


/**
 * Utility classes for activation functions. Used to convert a single value
 * to/from an array. This is necessary because the activation functions are
 * designed to operate on arrays, rather than single values.
 * 
 * @author jheaton
 * 
 */
public final class ActivationUtil {


	/**
	 * Private constructor.
	 */
	private ActivationUtil() {
	}
	
	/**
	 * Get a single value from an array. Return the first element in the 
	 * array.
	 * @param d The array.
	 * @return The first element in the array.
	 */
	public static double fromArray(final double[] d) {
		return d[0];
	}

	/**
	 * Take a single value and create an array that holds it.
	 * @param d The single value.
	 * @return The array.
	 */
	public static double[] toArray(final double d) {
		final double[] result = new double[1];
		result[0] = d;
		return result;
	}
}
