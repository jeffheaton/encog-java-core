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
