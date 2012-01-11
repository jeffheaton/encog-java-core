/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.svm;

/**
 * Supports both class and new support vector calculations, as well as one-class
 * distribution.
 * 
 * For more information about the two "new" support vector types, as well as the
 * one-class SVM, refer to the following articles.
 * 
 * B. Sch?lkopf, A. Smola, R. Williamson, and P. L. Bartlett. New support vector
 * algorithms. Neural Computation, 12, 2000, 1207-1245.
 * 
 * B. Sch?lkopf, J. Platt, J. Shawe-Taylor, A. J. Smola, and R. C. Williamson.
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
