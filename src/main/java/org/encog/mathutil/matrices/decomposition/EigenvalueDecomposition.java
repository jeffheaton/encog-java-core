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

import org.encog.mathutil.EncogMath;
import org.encog.mathutil.matrices.Matrix;

/**
 * Eigenvalues and eigenvectors of a real matrix.
 * <P>
 * If A is symmetric, then A = V*D*V' where the eigenvalue matrix D is diagonal
 * and the eigenvector matrix V is orthogonal. I.e. A =
 * V.times(D.times(V.transpose())) and V.times(V.transpose()) equals the
 * identity matrix.
 * <P>
 * If A is not symmetric, then the eigenvalue matrix D is block diagonal with
 * the real eigenvalues in 1-by-1 blocks and any complex eigenvalues, lambda +
 * i*mu, in 2-by-2 blocks, [lambda, mu; -mu, lambda]. The columns of V represent
 * the eigenvectors in the sense that A*V = V*D, i.e. A.times(V) equals
 * V.times(D). The matrix V may be badly conditioned, or even singular, so the
 * validity of the equation A = V*D*inverse(V) depends upon V.cond().
 * 
 * This file based on a class from the public domain JAMA package.
 * http://math.nist.gov/javanumerics/jama/
 */

public class EigenvalueDecomposition {

	/**
	 * Row and column dimension (square matrix).
	 */
	private final int n;

	/**
	 * Symmetry flag.
	 */
	private boolean issymmetric;

	/**
	 * Arrays for internal storage of eigenvalues.
	 */
	private final double[] d, e;

	/**
	 * Array for internal storage of eigenvectors.
	 */
	private final double[][] v;

	/**
	 * Complex scalar division.
	 */
	private double cdivr;

	/**
	 * Complex scalar division.
	 */
	private double cdivi;

	/**
	 * Array for internal storage of nonsymmetric Hessenberg form.
	 * 
	 * @serial internal storage of nonsymmetric Hessenberg form.
	 */
	private double[][] h;

	/**
	 * Working storage for nonsymmetric algorithm.
	 * 
	 * @serial working storage for nonsymmetric algorithm.
	 */
	private double[] ort;

	/**
	 * Check for symmetry, then construct the eigenvalue decomposition Structure
	 * to access D and V.
	 * 
	 * @param matrix
	 *            Square matrix
	 */
	public EigenvalueDecomposition(final Matrix matrix) {
		final double[][] a = matrix.getData();
		this.n = matrix.getCols();
		this.v = new double[this.n][this.n];
		this.d = new double[this.n];
		this.e = new double[this.n];

		this.issymmetric = true;
		for (int j = 0; (j < this.n) & this.issymmetric; j++) {
			for (int i = 0; (i < this.n) & this.issymmetric; i++) {
				this.issymmetric = (a[i][j] == a[j][i]);
			}
		}

		if (this.issymmetric) {
			for (int i = 0; i < this.n; i++) {
				for (int j = 0; j < this.n; j++) {
					this.v[i][j] = a[i][j];
				}
			}

			// Tridiagonalize.
			tred2();

			// Diagonalize.
			tql2();

		} else {
			this.h = new double[this.n][this.n];
			this.ort = new double[this.n];

			for (int j = 0; j < this.n; j++) {
				for (int i = 0; i < this.n; i++) {
					this.h[i][j] = a[i][j];
				}
			}

			// Reduce to Hessenberg form.
			orthes();

			// Reduce Hessenberg to real Schur form.
			hqr2();
		}
	}

	// Symmetric tridiagonal QL algorithm.

	private void cdiv(final double xr, final double xi, final double yr,
			final double yi) {
		double r, d;
		if (Math.abs(yr) > Math.abs(yi)) {
			r = yi / yr;
			d = yr + r * yi;
			this.cdivr = (xr + r * xi) / d;
			this.cdivi = (xi - r * xr) / d;
		} else {
			r = yr / yi;
			d = yi + r * yr;
			this.cdivr = (r * xr + xi) / d;
			this.cdivi = (r * xi - xr) / d;
		}
	}

	/**
	 * Return the block diagonal eigenvalue matrix
	 * 
	 * @return D
	 */

	public Matrix getD() {
		final Matrix X = new Matrix(this.n, this.n);
		final double[][] D = X.getData();
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.n; j++) {
				D[i][j] = 0.0;
			}
			D[i][i] = this.d[i];
			if (this.e[i] > 0) {
				D[i][i + 1] = this.e[i];
			} else if (this.e[i] < 0) {
				D[i][i - 1] = this.e[i];
			}
		}
		return X;
	}

	/**
	 * Return the imaginary parts of the eigenvalues.
	 * 
	 * @return imag(diag(D)).
	 */
	public double[] getImagEigenvalues() {
		return this.e;
	}

	/**
	 * Return the real parts of the eigenvalues.
	 * 
	 * @return real(diag(D)).
	 */
	public double[] getRealEigenvalues() {
		return this.d;
	}

	/**
	 * Return the eigenvector matrix.
	 * 
	 * @return V
	 */
	public Matrix getV() {
		return new Matrix(this.v);
	}

	/**
	 * This is derived from the Algol procedure hqr2, by Martin and Wilkinson,
	 * Handbook for Auto. Comp., Vol.ii-Linear Algebra, and the corresponding
	 * Fortran subroutine in EISPACK.
	 */
	private void hqr2() {

		// Initialize

		final int nn = this.n;
		int n = nn - 1;
		final int low = 0;
		final int high = nn - 1;
		final double eps = Math.pow(2.0, -52.0);
		double exshift = 0.0;
		double p = 0, q = 0, r = 0, s = 0, z = 0, t, w, x, y;

		// Store roots isolated by balanc and compute matrix norm

		double norm = 0.0;
		for (int i = 0; i < nn; i++) {
			if ((i < low) | (i > high)) {
				this.d[i] = this.h[i][i];
				this.e[i] = 0.0;
			}
			for (int j = Math.max(i - 1, 0); j < nn; j++) {
				norm = norm + Math.abs(this.h[i][j]);
			}
		}

		// Outer loop over eigenvalue index

		int iter = 0;
		while (n >= low) {

			// Look for single small sub-diagonal element

			int l = n;
			while (l > low) {
				s = Math.abs(this.h[l - 1][l - 1]) + Math.abs(this.h[l][l]);
				if (s == 0.0) {
					s = norm;
				}
				if (Math.abs(this.h[l][l - 1]) < eps * s) {
					break;
				}
				l--;
			}

			// Check for convergence
			// One root found

			if (l == n) {
				this.h[n][n] = this.h[n][n] + exshift;
				this.d[n] = this.h[n][n];
				this.e[n] = 0.0;
				n--;
				iter = 0;

				// Two roots found

			} else if (l == n - 1) {
				w = this.h[n][n - 1] * this.h[n - 1][n];
				p = (this.h[n - 1][n - 1] - this.h[n][n]) / 2.0;
				q = p * p + w;
				z = Math.sqrt(Math.abs(q));
				this.h[n][n] = this.h[n][n] + exshift;
				this.h[n - 1][n - 1] = this.h[n - 1][n - 1] + exshift;
				x = this.h[n][n];

				// Real pair

				if (q >= 0) {
					if (p >= 0) {
						z = p + z;
					} else {
						z = p - z;
					}
					this.d[n - 1] = x + z;
					this.d[n] = this.d[n - 1];
					if (z != 0.0) {
						this.d[n] = x - w / z;
					}
					this.e[n - 1] = 0.0;
					this.e[n] = 0.0;
					x = this.h[n][n - 1];
					s = Math.abs(x) + Math.abs(z);
					p = x / s;
					q = z / s;
					r = Math.sqrt(p * p + q * q);
					p = p / r;
					q = q / r;

					// Row modification

					for (int j = n - 1; j < nn; j++) {
						z = this.h[n - 1][j];
						this.h[n - 1][j] = q * z + p * this.h[n][j];
						this.h[n][j] = q * this.h[n][j] - p * z;
					}

					// Column modification

					for (int i = 0; i <= n; i++) {
						z = this.h[i][n - 1];
						this.h[i][n - 1] = q * z + p * this.h[i][n];
						this.h[i][n] = q * this.h[i][n] - p * z;
					}

					// Accumulate transformations

					for (int i = low; i <= high; i++) {
						z = this.v[i][n - 1];
						this.v[i][n - 1] = q * z + p * this.v[i][n];
						this.v[i][n] = q * this.v[i][n] - p * z;
					}

					// Complex pair

				} else {
					this.d[n - 1] = x + p;
					this.d[n] = x + p;
					this.e[n - 1] = z;
					this.e[n] = -z;
				}
				n = n - 2;
				iter = 0;

				// No convergence yet

			} else {

				// Form shift

				x = this.h[n][n];
				y = 0.0;
				w = 0.0;
				if (l < n) {
					y = this.h[n - 1][n - 1];
					w = this.h[n][n - 1] * this.h[n - 1][n];
				}

				// Wilkinson's original ad hoc shift

				if (iter == 10) {
					exshift += x;
					for (int i = low; i <= n; i++) {
						this.h[i][i] -= x;
					}
					s = Math.abs(this.h[n][n - 1])
							+ Math.abs(this.h[n - 1][n - 2]);
					x = y = 0.75 * s;
					w = -0.4375 * s * s;
				}

				// MATLAB's new ad hoc shift

				if (iter == 30) {
					s = (y - x) / 2.0;
					s = s * s + w;
					if (s > 0) {
						s = Math.sqrt(s);
						if (y < x) {
							s = -s;
						}
						s = x - w / ((y - x) / 2.0 + s);
						for (int i = low; i <= n; i++) {
							this.h[i][i] -= s;
						}
						exshift += s;
						x = y = w = 0.964;
					}
				}

				iter = iter + 1; // (Could check iteration count here.)

				// Look for two consecutive small sub-diagonal elements

				int m = n - 2;
				while (m >= l) {
					z = this.h[m][m];
					r = x - z;
					s = y - z;
					p = (r * s - w) / this.h[m + 1][m] + this.h[m][m + 1];
					q = this.h[m + 1][m + 1] - z - r - s;
					r = this.h[m + 2][m + 1];
					s = Math.abs(p) + Math.abs(q) + Math.abs(r);
					p = p / s;
					q = q / s;
					r = r / s;
					if (m == l) {
						break;
					}
					if (Math.abs(this.h[m][m - 1])
							* (Math.abs(q) + Math.abs(r)) < eps
							* (Math.abs(p) * (Math.abs(this.h[m - 1][m - 1])
									+ Math.abs(z) + Math
									.abs(this.h[m + 1][m + 1])))) {
						break;
					}
					m--;
				}

				for (int i = m + 2; i <= n; i++) {
					this.h[i][i - 2] = 0.0;
					if (i > m + 2) {
						this.h[i][i - 3] = 0.0;
					}
				}

				// Double QR step involving rows l:n and columns m:n

				for (int k = m; k <= n - 1; k++) {
					final boolean notlast = (k != n - 1);
					if (k != m) {
						p = this.h[k][k - 1];
						q = this.h[k + 1][k - 1];
						r = (notlast ? this.h[k + 2][k - 1] : 0.0);
						x = Math.abs(p) + Math.abs(q) + Math.abs(r);
						if (x != 0.0) {
							p = p / x;
							q = q / x;
							r = r / x;
						}
					}
					if (x == 0.0) {
						break;
					}
					s = Math.sqrt(p * p + q * q + r * r);
					if (p < 0) {
						s = -s;
					}
					if (s != 0) {
						if (k != m) {
							this.h[k][k - 1] = -s * x;
						} else if (l != m) {
							this.h[k][k - 1] = -this.h[k][k - 1];
						}
						p = p + s;
						x = p / s;
						y = q / s;
						z = r / s;
						q = q / p;
						r = r / p;

						// Row modification

						for (int j = k; j < nn; j++) {
							p = this.h[k][j] + q * this.h[k + 1][j];
							if (notlast) {
								p = p + r * this.h[k + 2][j];
								this.h[k + 2][j] = this.h[k + 2][j] - p * z;
							}
							this.h[k][j] = this.h[k][j] - p * x;
							this.h[k + 1][j] = this.h[k + 1][j] - p * y;
						}

						// Column modification

						for (int i = 0; i <= Math.min(n, k + 3); i++) {
							p = x * this.h[i][k] + y * this.h[i][k + 1];
							if (notlast) {
								p = p + z * this.h[i][k + 2];
								this.h[i][k + 2] = this.h[i][k + 2] - p * r;
							}
							this.h[i][k] = this.h[i][k] - p;
							this.h[i][k + 1] = this.h[i][k + 1] - p * q;
						}

						// Accumulate transformations

						for (int i = low; i <= high; i++) {
							p = x * this.v[i][k] + y * this.v[i][k + 1];
							if (notlast) {
								p = p + z * this.v[i][k + 2];
								this.v[i][k + 2] = this.v[i][k + 2] - p * r;
							}
							this.v[i][k] = this.v[i][k] - p;
							this.v[i][k + 1] = this.v[i][k + 1] - p * q;
						}
					} // (s != 0)
				} // k loop
			} // check convergence
		} // while (n >= low)

		// Backsubstitute to find vectors of upper triangular form

		if (norm == 0.0) {
			return;
		}

		for (n = nn - 1; n >= 0; n--) {
			p = this.d[n];
			q = this.e[n];

			// Real vector

			if (q == 0) {
				int l = n;
				this.h[n][n] = 1.0;
				for (int i = n - 1; i >= 0; i--) {
					w = this.h[i][i] - p;
					r = 0.0;
					for (int j = l; j <= n; j++) {
						r = r + this.h[i][j] * this.h[j][n];
					}
					if (this.e[i] < 0.0) {
						z = w;
						s = r;
					} else {
						l = i;
						if (this.e[i] == 0.0) {
							if (w != 0.0) {
								this.h[i][n] = -r / w;
							} else {
								this.h[i][n] = -r / (eps * norm);
							}

							// Solve real equations

						} else {
							x = this.h[i][i + 1];
							y = this.h[i + 1][i];
							q = (this.d[i] - p) * (this.d[i] - p) + this.e[i]
									* this.e[i];
							t = (x * s - z * r) / q;
							this.h[i][n] = t;
							if (Math.abs(x) > Math.abs(z)) {
								this.h[i + 1][n] = (-r - w * t) / x;
							} else {
								this.h[i + 1][n] = (-s - y * t) / z;
							}
						}

						// Overflow control

						t = Math.abs(this.h[i][n]);
						if ((eps * t) * t > 1) {
							for (int j = i; j <= n; j++) {
								this.h[j][n] = this.h[j][n] / t;
							}
						}
					}
				}

				// Complex vector

			} else if (q < 0) {
				int l = n - 1;

				// Last vector component imaginary so matrix is triangular

				if (Math.abs(this.h[n][n - 1]) > Math.abs(this.h[n - 1][n])) {
					this.h[n - 1][n - 1] = q / this.h[n][n - 1];
					this.h[n - 1][n] = -(this.h[n][n] - p) / this.h[n][n - 1];
				} else {
					cdiv(0.0, -this.h[n - 1][n], this.h[n - 1][n - 1] - p, q);
					this.h[n - 1][n - 1] = this.cdivr;
					this.h[n - 1][n] = this.cdivi;
				}
				this.h[n][n - 1] = 0.0;
				this.h[n][n] = 1.0;
				for (int i = n - 2; i >= 0; i--) {
					double ra, sa, vr, vi;
					ra = 0.0;
					sa = 0.0;
					for (int j = l; j <= n; j++) {
						ra = ra + this.h[i][j] * this.h[j][n - 1];
						sa = sa + this.h[i][j] * this.h[j][n];
					}
					w = this.h[i][i] - p;

					if (this.e[i] < 0.0) {
						z = w;
						r = ra;
						s = sa;
					} else {
						l = i;
						if (this.e[i] == 0) {
							cdiv(-ra, -sa, w, q);
							this.h[i][n - 1] = this.cdivr;
							this.h[i][n] = this.cdivi;
						} else {

							// Solve complex equations

							x = this.h[i][i + 1];
							y = this.h[i + 1][i];
							vr = (this.d[i] - p) * (this.d[i] - p) + this.e[i]
									* this.e[i] - q * q;
							vi = (this.d[i] - p) * 2.0 * q;
							if ((vr == 0.0) & (vi == 0.0)) {
								vr = eps
										* norm
										* (Math.abs(w) + Math.abs(q)
												+ Math.abs(x) + Math.abs(y) + Math
												.abs(z));
							}
							cdiv(x * r - z * ra + q * sa, x * s - z * sa - q
									* ra, vr, vi);
							this.h[i][n - 1] = this.cdivr;
							this.h[i][n] = this.cdivi;
							if (Math.abs(x) > (Math.abs(z) + Math.abs(q))) {
								this.h[i + 1][n - 1] = (-ra - w
										* this.h[i][n - 1] + q * this.h[i][n])
										/ x;
								this.h[i + 1][n] = (-sa - w * this.h[i][n] - q
										* this.h[i][n - 1])
										/ x;
							} else {
								cdiv(-r - y * this.h[i][n - 1], -s - y
										* this.h[i][n], z, q);
								this.h[i + 1][n - 1] = this.cdivr;
								this.h[i + 1][n] = this.cdivi;
							}
						}

						// Overflow control

						t = Math.max(Math.abs(this.h[i][n - 1]),
								Math.abs(this.h[i][n]));
						if ((eps * t) * t > 1) {
							for (int j = i; j <= n; j++) {
								this.h[j][n - 1] = this.h[j][n - 1] / t;
								this.h[j][n] = this.h[j][n] / t;
							}
						}
					}
				}
			}
		}

		// Vectors of isolated roots

		for (int i = 0; i < nn; i++) {
			if ((i < low) | (i > high)) {
				for (int j = i; j < nn; j++) {
					this.v[i][j] = this.h[i][j];
				}
			}
		}

		// Back transformation to get eigenvectors of original matrix

		for (int j = nn - 1; j >= low; j--) {
			for (int i = low; i <= high; i++) {
				z = 0.0;
				for (int k = low; k <= Math.min(j, high); k++) {
					z = z + this.v[i][k] * this.h[k][j];
				}
				this.v[i][j] = z;
			}
		}
	}

	/**
	 * This is derived from the Algol procedures orthes and ortran, by Martin
	 * and Wilkinson, Handbook for Auto. Comp., Vol.ii-Linear Algebra, and the
	 * corresponding Fortran subroutines in EISPACK.
	 */
	private void orthes() {

		final int low = 0;
		final int high = this.n - 1;

		for (int m = low + 1; m <= high - 1; m++) {

			// Scale column.

			double scale = 0.0;
			for (int i = m; i <= high; i++) {
				scale = scale + Math.abs(this.h[i][m - 1]);
			}
			if (scale != 0.0) {

				// Compute Householder transformation.

				double lh = 0.0;
				for (int i = high; i >= m; i--) {
					this.ort[i] = this.h[i][m - 1] / scale;
					lh += this.ort[i] * this.ort[i];
				}
				double g = Math.sqrt(lh);
				if (this.ort[m] > 0) {
					g = -g;
				}
				lh = lh - this.ort[m] * g;
				this.ort[m] = this.ort[m] - g;

				// Apply Householder similarity transformation
				// H = (I-u*u'/h)*H*(I-u*u')/h)

				for (int j = m; j < this.n; j++) {
					double f = 0.0;
					for (int i = high; i >= m; i--) {
						f += this.ort[i] * this.h[i][j];
					}
					f = f / lh;
					for (int i = m; i <= high; i++) {
						this.h[i][j] -= f * this.ort[i];
					}
				}

				for (int i = 0; i <= high; i++) {
					double f = 0.0;
					for (int j = high; j >= m; j--) {
						f += this.ort[j] * this.h[i][j];
					}
					f = f / lh;
					for (int j = m; j <= high; j++) {
						this.h[i][j] -= f * this.ort[j];
					}
				}
				this.ort[m] = scale * this.ort[m];
				this.h[m][m - 1] = scale * g;
			}
		}

		// Accumulate transformations (Algol's ortran).

		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.n; j++) {
				this.v[i][j] = (i == j ? 1.0 : 0.0);
			}
		}

		for (int m = high - 1; m >= low + 1; m--) {
			if (this.h[m][m - 1] != 0.0) {
				for (int i = m + 1; i <= high; i++) {
					this.ort[i] = this.h[i][m - 1];
				}
				for (int j = m; j <= high; j++) {
					double g = 0.0;
					for (int i = m; i <= high; i++) {
						g += this.ort[i] * this.v[i][j];
					}
					// Double division avoids possible underflow
					g = (g / this.ort[m]) / this.h[m][m - 1];
					for (int i = m; i <= high; i++) {
						this.v[i][j] += g * this.ort[i];
					}
				}
			}
		}
	}

	private void tql2() {

		// This is derived from the Algol procedures tql2, by
		// Bowdler, Martin, Reinsch, and Wilkinson, Handbook for
		// Auto. Comp., Vol.ii-Linear Algebra, and the corresponding
		// Fortran subroutine in EISPACK.

		for (int i = 1; i < this.n; i++) {
			this.e[i - 1] = this.e[i];
		}
		this.e[this.n - 1] = 0.0;

		double f = 0.0;
		double tst1 = 0.0;
		final double eps = Math.pow(2.0, -52.0);
		for (int l = 0; l < this.n; l++) {

			// Find small subdiagonal element

			tst1 = Math.max(tst1, Math.abs(this.d[l]) + Math.abs(this.e[l]));
			int m = l;
			while (m < this.n) {
				if (Math.abs(this.e[m]) <= eps * tst1) {
					break;
				}
				m++;
			}

			// If m == l, d[l] is an eigenvalue,
			// otherwise, iterate.

			if (m > l) {
				int iter = 0;
				do {
					iter = iter + 1; // (Could check iteration count here.)

					// Compute implicit shift

					double g = this.d[l];
					double p = (this.d[l + 1] - g) / (2.0 * this.e[l]);
					double r = EncogMath.hypot(p, 1.0);
					if (p < 0) {
						r = -r;
					}
					this.d[l] = this.e[l] / (p + r);
					this.d[l + 1] = this.e[l] * (p + r);
					final double dl1 = this.d[l + 1];
					double h = g - this.d[l];
					for (int i = l + 2; i < this.n; i++) {
						this.d[i] -= h;
					}
					f = f + h;

					// Implicit QL transformation.

					p = this.d[m];
					double c = 1.0;
					double c2 = c;
					double c3 = c;
					final double el1 = this.e[l + 1];
					double s = 0.0;
					double s2 = 0.0;
					for (int i = m - 1; i >= l; i--) {
						c3 = c2;
						c2 = c;
						s2 = s;
						g = c * this.e[i];
						h = c * p;
						r = EncogMath.hypot(p, this.e[i]);
						this.e[i + 1] = s * r;
						s = this.e[i] / r;
						c = p / r;
						p = c * this.d[i] - s * g;
						this.d[i + 1] = h + s * (c * g + s * this.d[i]);

						// Accumulate transformation.

						for (int k = 0; k < this.n; k++) {
							h = this.v[k][i + 1];
							this.v[k][i + 1] = s * this.v[k][i] + c * h;
							this.v[k][i] = c * this.v[k][i] - s * h;
						}
					}
					p = -s * s2 * c3 * el1 * this.e[l] / dl1;
					this.e[l] = s * p;
					this.d[l] = c * p;

					// Check for convergence.

				} while (Math.abs(this.e[l]) > eps * tst1);
			}
			this.d[l] = this.d[l] + f;
			this.e[l] = 0.0;
		}

		// Sort eigenvalues and corresponding vectors.

		for (int i = 0; i < this.n - 1; i++) {
			int k = i;
			double p = this.d[i];
			for (int j = i + 1; j < this.n; j++) {
				if (this.d[j] < p) {
					k = j;
					p = this.d[j];
				}
			}
			if (k != i) {
				this.d[k] = this.d[i];
				this.d[i] = p;
				for (int j = 0; j < this.n; j++) {
					p = this.v[j][i];
					this.v[j][i] = this.v[j][k];
					this.v[j][k] = p;
				}
			}
		}
	}

	/**
	 * Symmetric Householder reduction to tridiagonal form.
	 */
	private void tred2() {

		// This is derived from the Algol procedures tred2 by
		// Bowdler, Martin, Reinsch, and Wilkinson, Handbook for
		// Auto. Comp., Vol.ii-Linear Algebra, and the corresponding
		// Fortran subroutine in EISPACK.

		for (int j = 0; j < this.n; j++) {
			this.d[j] = this.v[this.n - 1][j];
		}

		// Householder reduction to tridiagonal form.

		for (int i = this.n - 1; i > 0; i--) {

			// Scale to avoid under/overflow.

			double scale = 0.0;
			double h = 0.0;
			for (int k = 0; k < i; k++) {
				scale = scale + Math.abs(this.d[k]);
			}
			if (scale == 0.0) {
				this.e[i] = this.d[i - 1];
				for (int j = 0; j < i; j++) {
					this.d[j] = this.v[i - 1][j];
					this.v[i][j] = 0.0;
					this.v[j][i] = 0.0;
				}
			} else {

				// Generate Householder vector.

				for (int k = 0; k < i; k++) {
					this.d[k] /= scale;
					h += this.d[k] * this.d[k];
				}
				double f = this.d[i - 1];
				double g = Math.sqrt(h);
				if (f > 0) {
					g = -g;
				}
				this.e[i] = scale * g;
				h = h - f * g;
				this.d[i - 1] = f - g;
				for (int j = 0; j < i; j++) {
					this.e[j] = 0.0;
				}

				// Apply similarity transformation to remaining columns.

				for (int j = 0; j < i; j++) {
					f = this.d[j];
					this.v[j][i] = f;
					g = this.e[j] + this.v[j][j] * f;
					for (int k = j + 1; k <= i - 1; k++) {
						g += this.v[k][j] * this.d[k];
						this.e[k] += this.v[k][j] * f;
					}
					this.e[j] = g;
				}
				f = 0.0;
				for (int j = 0; j < i; j++) {
					this.e[j] /= h;
					f += this.e[j] * this.d[j];
				}
				final double hh = f / (h + h);
				for (int j = 0; j < i; j++) {
					this.e[j] -= hh * this.d[j];
				}
				for (int j = 0; j < i; j++) {
					f = this.d[j];
					g = this.e[j];
					for (int k = j; k <= i - 1; k++) {
						this.v[k][j] -= (f * this.e[k] + g * this.d[k]);
					}
					this.d[j] = this.v[i - 1][j];
					this.v[i][j] = 0.0;
				}
			}
			this.d[i] = h;
		}

		// Accumulate transformations.

		for (int i = 0; i < this.n - 1; i++) {
			this.v[this.n - 1][i] = this.v[i][i];
			this.v[i][i] = 1.0;
			final double h = this.d[i + 1];
			if (h != 0.0) {
				for (int k = 0; k <= i; k++) {
					this.d[k] = this.v[k][i + 1] / h;
				}
				for (int j = 0; j <= i; j++) {
					double g = 0.0;
					for (int k = 0; k <= i; k++) {
						g += this.v[k][i + 1] * this.v[k][j];
					}
					for (int k = 0; k <= i; k++) {
						this.v[k][j] -= g * this.d[k];
					}
				}
			}
			for (int k = 0; k <= i; k++) {
				this.v[k][i + 1] = 0.0;
			}
		}
		for (int j = 0; j < this.n; j++) {
			this.d[j] = this.v[this.n - 1][j];
			this.v[this.n - 1][j] = 0.0;
		}
		this.v[this.n - 1][this.n - 1] = 1.0;
		this.e[0] = 0.0;
	}
}
