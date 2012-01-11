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

import org.encog.mathutil.matrices.Matrix;
import org.encog.mathutil.matrices.MatrixError;

/**
 * LU Decomposition.
 * <P>
 * For an m-by-n matrix A with m >= n, the LU decomposition is an m-by-n unit
 * lower triangular matrix L, an n-by-n upper triangular matrix U, and a
 * permutation vector piv of length m so that A(piv,:) = L*U. If m < n, then L
 * is m-by-m and U is m-by-n.
 * <P>
 * The LU decompostion with pivoting always exists, even if the matrix is
 * singular, so the constructor will never fail. The primary use of the LU
 * decomposition is in the solution of square systems of simultaneous linear
 * equations. This will fail if isNonsingular() returns false.
 * 
 * This file based on a class from the public domain JAMA package.
 * http://math.nist.gov/javanumerics/jama/
 */

public class LUDecomposition {

	/**
	 * Array for internal storage of decomposition.
	 */
	private double[][] LU;

	/**
	 * Row and column dimensions, and pivot sign.
	 */
	private int m, n, pivsign;

	/**
	 * Internal storage of pivot vector.
	 */
	private int[] piv;


	/**
	 * LU Decomposition
	 * Structure to access L, U and piv.
	 * @param A
	 *            Rectangular matrix
	 */

	public LUDecomposition(Matrix A) {

		// Use a "left-looking", dot-product, Crout/Doolittle algorithm.

		LU = A.getArrayCopy();
		m = A.getRows();
		n = A.getCols();
		piv = new int[m];
		for (int i = 0; i < m; i++) {
			piv[i] = i;
		}
		pivsign = 1;
		double[] LUrowi;
		double[] LUcolj = new double[m];

		// Outer loop.

		for (int j = 0; j < n; j++) {

			// Make a copy of the j-th column to localize references.

			for (int i = 0; i < m; i++) {
				LUcolj[i] = LU[i][j];
			}

			// Apply previous transformations.

			for (int i = 0; i < m; i++) {
				LUrowi = LU[i];

				// Most of the time is spent in the following dot product.

				int kmax = Math.min(i, j);
				double s = 0.0;
				for (int k = 0; k < kmax; k++) {
					s += LUrowi[k] * LUcolj[k];
				}

				LUrowi[j] = LUcolj[i] -= s;
			}

			// Find pivot and exchange if necessary.

			int p = j;
			for (int i = j + 1; i < m; i++) {
				if (Math.abs(LUcolj[i]) > Math.abs(LUcolj[p])) {
					p = i;
				}
			}
			if (p != j) {
				for (int k = 0; k < n; k++) {
					double t = LU[p][k];
					LU[p][k] = LU[j][k];
					LU[j][k] = t;
				}
				int k = piv[p];
				piv[p] = piv[j];
				piv[j] = k;
				pivsign = -pivsign;
			}

			// Compute multipliers.

			if (j < m & LU[j][j] != 0.0) {
				for (int i = j + 1; i < m; i++) {
					LU[i][j] /= LU[j][j];
				}
			}
		}
	}

	/**
	 * Is the matrix nonsingular?
	 * 
	 * @return true if U, and hence A, is nonsingular.
	 */

	public boolean isNonsingular() {
		for (int j = 0; j < n; j++) {
			if (LU[j][j] == 0)
				return false;
		}
		return true;
	}

	/**
	 * Return lower triangular factor
	 * 
	 * @return L
	 */

	public Matrix getL() {
		Matrix X = new Matrix(m, n);
		double[][] L = X.getData();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (i > j) {
					L[i][j] = LU[i][j];
				} else if (i == j) {
					L[i][j] = 1.0;
				} else {
					L[i][j] = 0.0;
				}
			}
		}
		return X;
	}

	/**
	 * Return upper triangular factor
	 * 
	 * @return U
	 */

	public Matrix getU() {
		Matrix X = new Matrix(n, n);
		double[][] U = X.getData();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i <= j) {
					U[i][j] = LU[i][j];
				} else {
					U[i][j] = 0.0;
				}
			}
		}
		return X;
	}

	/**
	 * Return pivot permutation vector
	 * 
	 * @return piv
	 */

	public int[] getPivot() {
		int[] p = new int[m];
		for (int i = 0; i < m; i++) {
			p[i] = piv[i];
		}
		return p;
	}

	/**
	 * Return pivot permutation vector as a one-dimensional double array
	 * 
	 * @return (double) piv
	 */

	public double[] getDoublePivot() {
		double[] vals = new double[m];
		for (int i = 0; i < m; i++) {
			vals[i] = (double) piv[i];
		}
		return vals;
	}

	/**
	 * Determinant
	 * 
	 * @return det(A)
	 * @exception IllegalArgumentException
	 *                Matrix must be square
	 */

	public double det() {
		if (m != n) {
			throw new IllegalArgumentException("Matrix must be square.");
		}
		double d = (double) pivsign;
		for (int j = 0; j < n; j++) {
			d *= LU[j][j];
		}
		return d;
	}

	/**
	 * Solve A*X = B
	 * 
	 * @param B
	 *            A Matrix with as many rows as A and any number of columns.
	 * @return X so that L*U*X = B(piv,:)
	 * @exception IllegalArgumentException
	 *                Matrix row dimensions must agree.
	 * @exception RuntimeException
	 *                Matrix is singular.
	 */

	public Matrix solve(Matrix B) {
		if (B.getRows() != m) {
			throw new IllegalArgumentException(
					"Matrix row dimensions must agree.");
		}
		if (!this.isNonsingular()) {
			throw new RuntimeException("Matrix is singular.");
		}

		// Copy right hand side with pivoting
		int nx = B.getCols();
		Matrix Xmat = B.getMatrix(piv, 0, nx - 1);
		double[][] X = Xmat.getData();

		// Solve L*Y = B(piv,:)
		for (int k = 0; k < n; k++) {
			for (int i = k + 1; i < n; i++) {
				for (int j = 0; j < nx; j++) {
					X[i][j] -= X[k][j] * LU[i][k];
				}
			}
		}
		// Solve U*X = Y;
		for (int k = n - 1; k >= 0; k--) {
			for (int j = 0; j < nx; j++) {
				X[k][j] /= LU[k][k];
			}
			for (int i = 0; i < k; i++) {
				for (int j = 0; j < nx; j++) {
					X[i][j] -= X[k][j] * LU[i][k];
				}
			}
		}
		return Xmat;
	}
	
	public double[] Solve(double[] value) {
		if (value == null) {
			throw new MatrixError("value");
		}

		if (value.length != this.LU.length) {
			throw new MatrixError("Invalid matrix dimensions.");
		}

		if (!this.isNonsingular()) {
			throw new MatrixError("Matrix is singular");
		}

		// Copy right hand side with pivoting
		int count = value.length;
		double[] b = new double[count];
		for (int i = 0; i < b.length; i++) {
			b[i] = value[piv[i]];
		}

		int rows = LU[0].length;
		int columns = LU[0].length;
		double[][] lu = LU;


		// Solve L*Y = B
		double[] X = new double[count];
		for (int i = 0; i < rows; i++) {
			X[i] = b[i];
			for (int j = 0; j < i; j++) {
				X[i] -= lu[i][j] * X[j];
			}
		}

		// Solve U*X = Y;
		for (int i = rows - 1; i >= 0; i--) {
			// double sum = 0.0;
			for (int j = columns - 1; j > i; j--) {
				X[i] -= lu[i][j] * X[j];
			}
			X[i] /= lu[i][i];
		}
		return X;
	}
	
    /**
     * Solves a set of equation systems of type <c>A * X = B</c>.
     * @return Matrix <c>X</c> so that <c>L * U * X = B</c>.
     */
    public double[][] inverse()
    {
        if (!this.isNonsingular())
        {
            throw new MatrixError("Matrix is singular");
        }

        int rows = this.LU.length;
        int columns = LU[0].length;
        int count = rows;
        double[][] lu = LU;

        double[][] X = new double[rows][columns];
        for (int i = 0; i < rows; i++)
        {
            int k = this.piv[i];
            X[i][k] = 1.0; 
        }

        // Solve L*Y = B(piv,:)
        for (int k = 0; k < columns; k++)
        {
            for (int i = k + 1; i < columns; i++)
            {
                for (int j = 0; j < count; j++)
                {
                    X[i][j] -= X[k][j] * lu[i][k];
                }
            }
        }

        // Solve U*X = Y;
        for (int k = columns - 1; k >= 0; k--)
        {
            for (int j = 0; j < count; j++)
            {
                X[k][j] /= lu[k][k];
            }

            for (int i = 0; i < k; i++)
            {
                for (int j = 0; j < count; j++)
                {
                    X[i][j] -= X[k][j] * lu[i][k];
                }
            }
        }

        return X;
    }
}
