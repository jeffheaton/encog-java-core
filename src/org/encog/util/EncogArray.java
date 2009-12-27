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
package org.encog.util;

public class EncogArray {
	
	/**
	 * Completely copy one array into another.
	 * 
	 * @param src
	 *            Source array.
	 * @param dst
	 *            Destination array.
	 */
	public static final void arrayCopy(double[] src, double[] dst) {
		System.arraycopy(src, 0, dst, 0, src.length);
	}
	
	/**
	 * Calculate the product of two vectors (a scalar value).
	 * 
	 * @param a
	 *            First vector to multiply.
	 * @param b
	 *            Second vector to multiply.
	 * @return The product of the two vectors (a scalar value).
	 */
	public static final double vectorProduct(double[] a, double[] b) {
		int length = a.length;
		double value = 0;

		for (int i = 0; i < length; ++i)
			value += a[i] * b[i];

		return value;
	}

	
	public static Double[] doubleToObject(double[] array) {
		Double[] result = new Double[array.length];
		for(int i=0;i<array.length;i++) {
			result[i] = new Double(array[i]);
		}
		return result;
	}
	
	public static double[] objectToDouble(Double[] array) {
		double[] result = new double[array.length];
		for(int i=0;i<array.length;i++) {
			result[i] = new Double(array[i]);
		}		
		return result;		
	}
}
