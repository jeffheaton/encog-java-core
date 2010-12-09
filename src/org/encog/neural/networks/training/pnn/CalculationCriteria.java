package org.encog.neural.networks.training.pnn;

public interface CalculationCriteria {

	double calcErrorWithSingleSigma(double sigma);


	double calcErrorWithMultipleSigma(double[] x, double[] direc,
			double[] deriv22, boolean b);
	
}
