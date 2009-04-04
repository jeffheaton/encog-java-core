package org.encog.util.math;

public class BoundMath {
	
	public static double exp(double d)
	{
		return BoundNumbers.bound(d);
	}

	public static double pow(double a, double b) {
		return BoundNumbers.bound(Math.pow(a, b));
	}

	public static double log(double a) {
		return BoundNumbers.bound(Math.log(a));
	}

	public static double sin(double a) {
		return BoundNumbers.bound(Math.sin(a));
	}
	
	public static double cos(double a) {
		return BoundNumbers.bound(Math.cos(a));
	}
}
