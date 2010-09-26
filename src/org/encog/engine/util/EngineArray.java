/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

package org.encog.engine.util;

import java.util.Arrays;
import java.util.Collection;

/**
 * Some array functions used by Encog.
 */
public final class EngineArray {

	/**
	 * Copy a double array.
	 * @param input The array to copy.
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
	 * Copy an int array.
	 * @param input The array to copy.
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
	 * @param array The array to fill.
	 * @param value What to fill the array with.
	 */
	public static void fill(final double[] array, final double value) {
		Arrays.fill(array, value);

	}
	
	/**
	 * Fill a float array.
	 * @param array The array to fill.
	 * @param value What to fill the array with.
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
	public static double[] listToDouble(final Collection< ? > list) {
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

	public static double[][] arrayCopy(double[][] source) {
		double[][] result = new double[source.length][source[0].length];
		
		for(int row = 0; row<source.length; row++)
		{
			for(int col = 0; col<source[0].length; col++)
			{
				result[row][col] = source[row][col];
			}
		}
		
		return result;
	}
}
