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
package org.encog.ml.fitting.gaussian;

import org.encog.mathutil.matrices.Matrix;
import org.encog.mathutil.matrices.MatrixMath;
import org.encog.ml.MLError;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.util.simple.EncogUtility;

public class GaussianFitting implements MLRegression {
	
	private double[] weights;
	private int inputCount;
	private final Matrix sigma;
	private final Matrix mu;
	private Matrix sigmaInverse;
	private double dimFactor;
	private double normConst;
	
	public GaussianFitting(int theInputCount) {
		this.mu = new Matrix(1,theInputCount);
		this.sigma = new Matrix( theInputCount,theInputCount );
		this.inputCount = theInputCount;
		this.weights = new double[theInputCount+1];
	}
	
	
	public double[] getWeights() {
		return weights;
	}

	@Override
	public int getInputCount() {
		return this.inputCount;
	}

	@Override
	public int getOutputCount() {
		return 1;
	}

	@Override
	public MLData compute(MLData input) {
		BasicMLData result = new BasicMLData(1);
		
		Matrix m1 = Matrix.createRowMatrix(input.getData());
		Matrix m2 = MatrixMath.subtract(m1, this.mu);
		Matrix m3 = MatrixMath.transpose(m2);
		Matrix m4 = MatrixMath.multiply(sigmaInverse, m3);
		Matrix m5 = MatrixMath.multiply(m4, m2);
		
		result.setData(0, m5.get(0, 0));
		
		/* double d1 = x.minus(mu).transpose().times
		  (sigmaInverse).times(x.minus(mu)).get(0,0);
		
		double d2 = Math.exp(-0.5*d1) / normConst;
		*/
		
		return result;
	}


	/**
	 * @return the sigma
	 */
	public Matrix getSigma() {
		return sigma;
	}


	/**
	 * @return the mu
	 */
	public Matrix getMu() {
		return mu;
	}
	
	public void finalizeTraining() {
		this.sigmaInverse = this.sigma.inverse();
		this.dimFactor = Math.pow(2 * Math.PI, ((double)this.getInputCount()) / 2.0);
		this.normConst = Math.sqrt(MatrixMath.determinant(sigma)) * dimFactor;
	}
	
}
