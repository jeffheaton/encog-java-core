package org.encog.neural.networks.svm;

/**
 * The type of SVM kernel in use.
 */
public enum KernelType {
	/**
	 * Linear kernel.
	 */
	Linear,
	
	/**
	 * Poly kernel.
	 */
	Poly,
	
	/**
	 * Radial basis function kernel.
	 */
	RadialBasisFunction,
	
	/**
	 * Sigmoid kernel.
	 */
	Sigmoid,
	
	/**
	 * Precomputed kernel.
	 */
	Precomputed 
}
