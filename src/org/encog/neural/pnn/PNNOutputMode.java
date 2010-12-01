package org.encog.neural.pnn;

/**
 * The output mode that will be used by the PNN.
 */
public enum PNNOutputMode {
	/**
	 * Unsupervised training will make use of autoassociation. No "ideal" values
	 * should be provided for training. Input and output neuron counts must
	 * match.
	 */
	Unsupervised,

	/**
	 * Regression is where the neural network performs as a function. Input is
	 * supplied, and output is returned. The output is a numeric value.
	 */
	Regression,

	/**
	 * Classification attempts to classify the input into a number of predefined
	 * classes. The class is stored in the ideal as a single "double" value,
	 * though it is really treated as an integer that represents class
	 * membership. The number of output neurons should match the number of
	 * classes.  Classes are indexed beginning at 0.
	 */
	Classification
}
