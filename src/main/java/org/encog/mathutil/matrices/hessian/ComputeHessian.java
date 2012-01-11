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
package org.encog.mathutil.matrices.hessian;

import org.encog.mathutil.matrices.Matrix;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;

/**
 * Compute (estimate) the Hessian matrix. The Hessian matrix is a matrix of the second
 * derivatives of the neural network. This is a square matrix with rows and columns
 * equal to the number of weights in the neural network.
 * 
 * A Hessian matrix is useful for several neural network functions.  It is also used
 * by the Levenberg Marquardt training method. 
 * 
 * http://en.wikipedia.org/wiki/Hessian_matrix
 */
public interface ComputeHessian {
	
	/**
	 * Init the class.  
	 * @param theNetwork The neural network to train.
	 * @param theTraining The training set to train with.
	 */
	void init(BasicNetwork theNetwork, MLDataSet theTraining);
	
	/**
	 * Compute the Hessian.
	 */
	void compute();
	
	/**
	 * The gradeints.
	 * @return The gradients for the Hessian.
	 */
	double[] getGradients();
	
	/**
	 * @return The sum of squares error over all of the training elements.
	 */
	double getSSE();
	
	/**
	 * Clear the Hessian and gradients.
	 */
	void clear();
	
	/**
	 * @return The Hessian matrix.
	 */
	public Matrix getHessianMatrix();

	/**
	 * @return Get the Hessian as a 2d array.
	 */
	public double[][] getHessian();
	
}
