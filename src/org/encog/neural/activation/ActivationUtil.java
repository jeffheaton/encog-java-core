package org.encog.neural.activation;

public class ActivationUtil {
	public static double[] toArray(double d)
	{
		double[] result = new double[1];
		result[0] = d;
		return result;
	}
	
	public static double fromArray(double[] d)
	{
		return d[0];
	}
}
