/*
 * Encog(tm) Core v2.4
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.mathutil.matrices.decomposition;

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
public class CholeskyDecomposition {


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

	public boolean isSPD() {
		return isspd;
	}

	/**
	 * Return triangular factor.
	 * 
	 * @return L
	 */

	public Matrix getL() {
		return new Matrix(l);
	}

	/**
	 * Solve A*X = B.
	 * 
	 * @param b
	 *            A Matrix with as many rows as A and any number of columns.
	 * @return X so that L*L'*X = b.
	 */
	public Matrix solve(final Matrix b) {
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
}
