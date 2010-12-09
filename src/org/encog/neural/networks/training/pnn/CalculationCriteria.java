package org.encog.neural.networks.training.pnn;

public interface CalculationCriteria {

	/**
	 * Calculate the error with a single sigma.
	 * @param sigma The sigma.
	 * @return The error.
	 */
	double calcErrorWithSingleSigma(double sigma);


	/**
	 * Calculate the error with multiple sigmas.
	 * @param x The data.
	 * @param direc The first derivative.
	 * @param deriv2 The 2nd derivatives.
	 * @param b Calculate the derivative.
	 * @return The error.
	 */
	double calcErrorWithMultipleSigma(double[] x, double[] direc,
			double[] deriv2, boolean b);
	
}
