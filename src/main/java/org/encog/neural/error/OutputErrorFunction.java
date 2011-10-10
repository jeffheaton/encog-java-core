package org.encog.neural.error;

public class OutputErrorFunction implements ErrorFunction {

	@Override
	public void calculateError(double[] ideal, double[] actual, double[] error) {
		for(int i=0;i<actual.length;i++) {
			error[i] = actual[i];
		}	
	}

}
