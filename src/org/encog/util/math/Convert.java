package org.encog.util.math;

public class Convert {
	public static double string2double(String str) {
		double result = 0;
		try {
			if (str != null) {
				result = Double.parseDouble(str);
			}
		} catch (NumberFormatException e) {
			// just let it equal zero
		}
		return result;
	}
	
	public static int string2int(String str) {
		int result = 0;
		try {
			if (str != null) {
				result = Integer.parseInt(str);
			}
		} catch (NumberFormatException e) {
			// just let it equal zero
		}
		return result;
	}
}
