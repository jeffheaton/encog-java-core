package org.encog.neural.pnn;

/**
 * Specifies the kernel type for the PNN.
 */
public enum PNNKernelType 
{
	/**
	 * A Gaussian curved kernel.  The usual choice.
	 */
	Gaussian,
	
	/**
	 * A steep kernel.
	 */
	Reciprocal
}
