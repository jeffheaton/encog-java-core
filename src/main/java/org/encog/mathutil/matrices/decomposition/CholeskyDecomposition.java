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
package org.encog.mathutil.matrices.decomposition;

import java.io.Serializable;

import org.encog.mathutil.matrices.Matrix;
import org.encog.mathutil.matrices.MatrixError;

/**
 * 
 * Cholesky Decomposition.
 * 
 * For a symmetric, positive definite matrix A, the Cholesky decomposition is an
 * lower triangular matrix L so that A = L*L'.
 * 
 * If the matrix is not symmetric or positive definite, the constructor returns
 * a partial decomposition and sets an internal flag that may be queried by the
 * isSPD() method.
 * 
 * This file based on a class from the public domain JAMA package.
 * http://math.nist.gov/javanumerics/jama/
 */
public class CholeskyDecomposition implements Serializable {


	/**
	 * Array for internal storage of decomposition.
	 */
	private double[][] l;

	/**
	 * Row and column dimension (square matrix).
	 */
	private int n;

	/**
	 * Symmetric and positive definite flag.
	 */
	private boolean isspd;

	/**
	 * Cholesky algorithm for symmetric and positive definite matrix.
	 * 
	 * @param matrix
	 *            Square, symmetric matrix.
	 */

	public CholeskyDecomposition(final Matrix matrix) {

		// Initialize.
		double[][] a = matrix.getData();
		n = matrix.getRows();
		l = new double[n][n];
		isspd = (matrix.getCols() == n);
		// Main loop.
		for (int j = 0; j < n; j++) {
			double[] lrowj = l[j];
			double d = 0.0;
			for (int k = 0; k < j; k++) {
				double[] lrowk = l[k];
				double s = 0.0;
				for (int i = 0; i < k; i++) {
					s += lrowk[i] * lrowj[i];
				}
				s = (a[j][k] - s) / l[k][k];
				lrowj[k] = s;
				d = d + s * s;
				isspd = isspd & (a[k][j] == a[j][k]);
			}
			d = a[j][j] - d;
			isspd = isspd & (d > 0.0);
			l[j][j] = Math.sqrt(Math.max(d, 0.0));
			for (int k = j + 1; k < n; k++) {
				l[j][k] = 0.0;
			}
		}
	}


	/**
	 * Is the matrix symmetric and positive definite?
	 * 
	 * @return true if A is symmetric and positive definite.
	 */

	public final boolean isSPD() {
		return isspd;
	}

	/**
	 * Return triangular factor.
	 * 
	 * @return L
	 */

	public final Matrix getL() {
		return new Matrix(l);
	}

	/**
	 * Solve A*X = B.
	 * 
	 * @param b
	 *            A Matrix with as many rows as A and any number of columns.
	 * @return X so that L*L'*X = b.
	 */
	public final Matrix solve(final Matrix b) {
		if (b.getRows() != n) {
			throw new MatrixError(
					"Matrix row dimensions must agree.");
		}
		if (!isspd) {
			throw new RuntimeException(
					"Matrix is not symmetric positive definite.");
		}

		// Copy right hand side.
		double[][] x = b.getArrayCopy();
		int nx = b.getCols();

		// Solve L*Y = B;
		for (int k = 0; k < n; k++) {
			for (int j = 0; j < nx; j++) {
				for (int i = 0; i < k; i++) {
					x[k][j] -= x[i][j] * l[k][i];
				}
				x[k][j] /= l[k][k];
			}
		}

		// Solve L'*X = Y;
		for (int k = n - 1; k >= 0; k--) {
			for (int j = 0; j < nx; j++) {
				for (int i = k + 1; i < n; i++) {
					x[k][j] -= x[i][j] * l[i][k];
				}
				x[k][j] /= l[k][k];
			}
		}

		return new Matrix(x);
	}


	public double getDeterminant() {		
		double result = 1;
		
		for (int i = 0; i < n; i++)
			result *= l[i][i];
		
		return result * result;
	}
	

	public Matrix inverseCholesky()
	{		
		double[][] li = lowerTriangularInverse(l);
		double[][] ic = new double[n][n];
		
		for (int r = 0; r < n; r++)
			for (int c = 0; c < n; c++)
				for (int i = 0; i < n; i++)
					ic[r][c] += li[i][r] * li[i][c];
		
		return new Matrix(ic);
	}
	
	
	private double[][] lowerTriangularInverse(double[][] m)
	{

		double[][] lti = new double[m.length][m.length];
		
		for (int j = 0; j < m.length; j++) {
			if (m[j][j] == 0)
				throw new IllegalArgumentException("Error, the matrix is not full rank");
			
			lti[j][j] = 1. / m[j][j];
			
			for (int i = j + 1; i < m.length; i++) {
				double sum = 0.;
				
				for (int k = j; k < i; k++)
					sum -= m[i][k] * lti[k][j];
				
				lti[i][j] = sum / m[i][i];
			}
		}
		
		return lti;
		
	}
}
