package org.encog.neural.networks.svm;

/**
 * Supports both class and new support vector calculations, as well as one-class
 * distribution.
 * 
 * For more information about the two "new" support vector types, as well as the
 * one-class SVM, refer to the following articles.
 * 
 * B. Schölkopf, A. Smola, R. Williamson, and P. L. Bartlett. New support vector
 * algorithms. Neural Computation, 12, 2000, 1207-1245.
 * 
 * B. Schölkopf, J. Platt, J. Shawe-Taylor, A. J. Smola, and R. C. Williamson.
 * Estimating the support of a high-dimensional distribution. Neural
 * Computation, 13, 2001, 1443-1471.
 * 
 */
public enum SVMType {

	/**
	 * Support vector for classification.
	 */
	SupportVectorClassification,

	/**
	 * New support vector for classification. For more information see the
	 * citations in the class header.
	 */
	NewSupportVectorClassification,

	/**
	 * One class distribution estimation.
	 */
	SupportVectorOneClass,

	/**
	 * Support vector for regression. Use Epsilon.
	 */
	EpsilonSupportVectorRegression,

	/**
	 * A "new" support vector machine for regression. For more information see
	 * the citations in the class header.
	 */
	NewSupportVectorRegression

}
