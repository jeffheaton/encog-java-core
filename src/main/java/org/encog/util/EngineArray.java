/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.util;

import java.util.Arrays;
import java.util.Collection;

/**
 * Some array functions used by Encog.
 */
public final class EngineArray {

	/**
	 * Copy a double array.
	 * 
	 * @param input
	 *            The array to copy.
	 * @return The result of the copy.
	 */
	public static double[] arrayCopy(final double[] input) {
		final double[] result = new double[input.length];
		EngineArray.arrayCopy(input, result);
		return result;
	}

	/**
	 * Completely copy one array into another.
	 * 
	 * @param src
	 *            Source array.
	 * @param dst
	 *            Destination array.
	 */
	public static void arrayCopy(final double[] src, final double[] dst) {
		System.arraycopy(src, 0, dst, 0, src.length);
	}

	/**
	 * Copy an array of floats to an array of doubles.
	 * 
	 * @param source
	 *            The source array.
	 * @param target
	 *            The target array.
	 */
	public static void arrayCopy(final double[] source, final float[] target) {
		for (int i = 0; i < source.length; i++) {
			target[i] = (float) source[i];
		}
	}

	/**
	 * Copy an array of doubles.
	 * 
	 * @param source
	 *            The source.
	 * @param sourcePos
	 *            The source index.
	 * @param target
	 *            The target.
	 * @param targetPos
	 *            The target index.
	 * @param length
	 *            The length.
	 */
	public static void arrayCopy(final double[] source, final int sourcePos,
			final double[] target, final int targetPos, final int length) {
		System.arraycopy(source, sourcePos, target, targetPos, length);

	}

	/**
	 * Copy a 2D double array.
	 * @param source The source.
	 * @return The copied array.
	 */
	public static double[][] arrayCopy(final double[][] source) {
		final double[][] result = new double[source.length][source[0].length];

		for (int row = 0; row < source.length; row++) {
			for (int col = 0; col < source[0].length; col++) {
				result[row][col] = source[row][col];
			}
		}

		return result;
	}

	/**
	 * Copy an array of floats to an array of doubles.
	 * 
	 * @param source
	 *            The source array.
	 * @param target
	 *            The target array.
	 */
	public static void arrayCopy(final float[] source, final double[] target) {
		for (int i = 0; i < source.length; i++) {
			target[i] = source[i];
		}
	}

	/**
	 * Copy an int array.
	 * 
	 * @param input
	 *            The array to copy.
	 * @return The result of the copy.
	 */
	public static int[] arrayCopy(final int[] input) {
		final int[] result = new int[input.length];
		EngineArray.arrayCopy(input, result);
		return result;
	}

	/**
	 * Completely copy one array into another.
	 * 
	 * @param src
	 *            Source array.
	 * @param dst
	 *            Destination array.
	 */
	public static void arrayCopy(final int[] src, final int[] dst) {
		System.arraycopy(src, 0, dst, 0, src.length);
	}

	/**
	 * Convert an array of double primitives to Double objects.
	 * 
	 * @param array
	 *            The primitive array.
	 * @return The object array.
	 */
	public static Double[] doubleToObject(final double[] array) {
		final Double[] result = new Double[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = new Double(array[i]);
		}
		return result;
	}

	/**
	 * Fill a double array.
	 * 
	 * @param array
	 *            The array to fill.
	 * @param value
	 *            What to fill the array with.
	 */
	public static void fill(final double[] array, final double value) {
		Arrays.fill(array, value);

	}

	/**
	 * Fill a float array.
	 * 
	 * @param array
	 *            The array to fill.
	 * @param value
	 *            What to fill the array with.
	 */
	public static void fill(final float[] array, final float value) {
		Arrays.fill(array, value);

	}

	/**
	 * Search for a string in an array.
	 * 
	 * @param search
	 *            Where to search.
	 * @param searchFor
	 *            What we are looking for.
	 * @return The index that the string occurs at.
	 */
	public static int findStringInArray(final String[] search,
			final String searchFor) {
		for (int i = 0; i < search.length; i++) {
			if (search[i].equals(searchFor)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Convert the collection to an array list of doubles.
	 * 
	 * @param list
	 *            The list to convert.
	 * @return The array of doubles.
	 */
	public static double[] listToDouble(final Collection<?> list) {
		final double[] result = new double[list.size()];
		int index = 0;
		for (final Object obj : list) {
			result[index++] = (Double) obj;
		}

		return result;
	}

	/**
	 * Convert an array of Double objects to double primitives.
	 * 
	 * @param array
	 *            An array of Double objects.
	 * @return An array of double primitives.
	 */
	public static double[] objectToDouble(final Double[] array) {
		final double[] result = new double[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = new Double(array[i]);
		}
		return result;
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
	public static double vectorProduct(final double[] a, final double[] b) {
		final int length = a.length;
		double value = 0;

		for (int i = 0; i < length; ++i) {
			value += a[i] * b[i];
		}

		return value;
	}

	/**
	 * Private constructor.
	 */
	private EngineArray() {

	}

	public static int indexOfLargest(double[] data) {
		int result = -1;
		
		for(int i=0;i<data.length;i++) {
			if( result==-1 || data[i]>data[result] )
				result = i;
		}
		
		return result;
	}

	public static double min(double[] weights) {
		double result = Double.MAX_VALUE;
		for(int i=0;i<weights.length;i++) {
			result = Math.min(result, weights[i]);
		}
		return result;
	}
	
	public static double max(double[] weights) {
		double result = Double.MIN_VALUE;
		for(int i=0;i<weights.length;i++) {
			result = Math.max(result, weights[i]);
		}
		return result;
	}

	public static boolean contains(int[] array, int target) {
		for(int i=0;i<array.length;i++) {
			if( array[i]==target) {
				return true;
			}
		}
		
		return false;
	}

	public static int maxIndex(double[] data) {
		int result = -1;
		for(int i=0;i<data.length;i++) {
			if( result==-1 || data[i]>data[result] ) {
				result = i;
			}
		}
		return result;
	}

	public static int maxIndex(int[] data) {
		int result = -1;
		for(int i=0;i<data.length;i++) {
			if( result==-1 || data[i]>data[result] ) {
				result = i;
			}
		}
		return result;
	}

	public static int max(int[] data) {
		int result = Integer.MIN_VALUE;
		for(int i=0;i<data.length;i++) {
			result = Math.max(result, data[i]);
		}
		return result;
	}
	
	public static int min(int[] data) {
		int result = Integer.MAX_VALUE;
		for(int i=0;i<data.length;i++) {
			result = Math.min(result, data[i]);
		}
		return result;
	}

	public static double mean(int[] data) {
		double result = 0;
		for(int i=0;i<data.length;i++) {
			result+=(double)data[i];
		}
		return result/(double)data.length;
	}

	public static double sdev(int[] data) {
		double avg = mean(data);
		double result = 0;
		for (int i = 0; i < data.length; i++) {
			double diff = data[i] - avg;
			result += diff * diff;
		}
		return Math.sqrt(result/(double)data.length);
	}

	public static double euclideanDistance(double[] p1, double[] p2) {
		double sum = 0;
		for(int i=0;i<p1.length;i++) {
			double d = p1[i] - p2[i];
			sum+=d*d;
		}
		return Math.sqrt(sum);
	}

	public static void fill(double[][] sigma, int value) {
		for(int i=0;i<sigma.length;i++) {
			for(int j=0;j<sigma[i].length;j++) {
				sigma[i][j] = value;
			}
		}
		
	}

	public static void fill(boolean[] a, boolean b) {
		for(int i=0;i<a.length;i++) {
			a[i] = b;
		}
	}

	public static double[] add(double[] d, double[] m) {
		double[] result = new double[d.length];
		for(int i=0;i<d.length;i++) {
			result[i] = d[i] + m[i];
		}
		return result;
	}

	public static double[] subtract(double[] a, double[] b) {
		double[] result = new double[a.length];
		for(int i=0;i<a.length;i++) {
			result[i] = a[i] - b[i];
		}
		return result;
	}
}
