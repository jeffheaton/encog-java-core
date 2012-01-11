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
import org.encog.neural.flat.FlatNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.EngineArray;

/**
 * Some basic code used to calculate Hessian matrixes.
 */
public abstract class BasicHessian implements ComputeHessian {
	
	/**
	 * The training data that provides the ideal values.
	 */
	protected MLDataSet training;
	
	/**
	 * The neural network that we would like to train.
	 */
	protected BasicNetwork network;

	
	/**
	 * The sum of square error.
	 */	
	protected double sse;

	/**
	 * The gradients of the Hessian.
	 */
	protected double[] gradients;
	
	/**
	 * The Hessian matrix.
	 */
	protected Matrix hessianMatrix;
	
	/**
	 * The Hessian 2d array.
	 */
	protected double[][] hessian;
	
	/**
	 * The derivatives.
	 */
	protected double[] derivative;
	
	/**
	 * The flat network.
	 */
	protected FlatNetwork flat;

	/**
	 * {@inheritDoc}
	 */
	public void init(BasicNetwork theNetwork, MLDataSet theTraining) {
		
		int weightCount = theNetwork.getStructure().getFlat().getWeights().length;
		this.flat = theNetwork.getFlat();
		this.training = theTraining;
		this.network = theNetwork;
		this.gradients = new double[weightCount];	
		this.hessianMatrix = new Matrix(weightCount,weightCount);
		this.hessian = this.hessianMatrix.getData();
		this.derivative = new double[weightCount];
	}
	
	/**
	 * {@inheritDoc}
	 */
	public double[] getGradients() {
		return gradients;
	}
		
	/**
	 * {@inheritDoc}
	 */
	public Matrix getHessianMatrix() {
		return hessianMatrix;
	}

	/**
	 * {@inheritDoc}
	 */
	public double[][] getHessian() {
		return hessian;
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		EngineArray.fill(this.gradients, 0);
		this.hessianMatrix.clear();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public double getSSE() {
		return sse;
	}
	
	/**
	 * Update the Hessian, sum's with what is in the Hessian already.  Call clear to clear out old Hessian.
	 * @param d
	 */
	public void updateHessian(double[] d) {
		// update the hessian
		int weightCount = this.network.getFlat().getWeights().length;
		for(int i=0;i<weightCount;i++) {
			for(int j=0;j<weightCount;j++) {
				this.hessian[i][j]+=2*d[i]*d[j];
			}
		}
	}
	
}
