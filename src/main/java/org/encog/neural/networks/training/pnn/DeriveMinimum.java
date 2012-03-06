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
package org.encog.neural.networks.training.pnn;

import org.encog.Encog;
import org.encog.util.EngineArray;
import org.encog.util.logging.EncogLogging;

/**
 * This class determines optimal values for multiple sigmas in a PNN kernel.
 * This is done using a CJ (conjugate gradient) method.
 * 
 * 
 * Some of the algorithms in this class are based on C++ code from:
 * 
 * Advanced Algorithms for Neural Networks: A C++ Sourcebook by Timothy Masters
 * John Wiley & Sons Inc (Computers); April 3, 1995 ISBN: 0471105880
 */
public class DeriveMinimum {

	/**
	 * Derive the minimum, using a conjugate gradient method.
	 * 
	 * @param maxIterations
	 * 	The max iterations.
	 * @param maxError
	 *            Stop at this error rate.
	 * @param eps
	 *            The machine's precision.
	 * @param tol
	 *            The convergence tolerance.
	 * @param network
	 *            The network to get the error from.
	 * @param n
	 *            The number of variables.
	 * @param x
	 *            The independent variable.
	 * @param ystart
	 *            The start for y.
	 * @param base
	 *            Work vector, must have n elements.
	 * @param direc
	 *            Work vector, must have n elements.
	 * @param g
	 *            Work vector, must have n elements.
	 * @param h
	 *            Work vector, must have n elements.
	 * @param deriv2
	 *            Work vector, must have n elements.
	 * @return The best error.
	 */
	public double calculate(final int maxIterations, final double maxError,
			final double eps, final double tol, final CalculationCriteria network,
			final int n, final double[] x, final double ystart,
			final double[] base, final double[] direc, final double[] g,
			final double[] h, final double[] deriv2) {
		double prevBest, toler, gam, improvement;
		final GlobalMinimumSearch globalMinimum = new GlobalMinimumSearch();

		double fbest = network.calcErrorWithMultipleSigma(x, direc, deriv2, true);
		prevBest = 1.e30;
		for (int i = 0; i < n; i++) {
			direc[i] = -direc[i];
		}

		EngineArray.arrayCopy(direc, g);
		EngineArray.arrayCopy(direc, h);

		int convergenceCounter = 0;
		int poorCJ = 0;

		// Main loop
		for (int iteration = 0; iteration < maxIterations; iteration++) {			
			
			if (fbest < maxError) {
				break;
			}
			
			EncogLogging.log(EncogLogging.LEVEL_INFO,
					"Beginning internal Iteration #" + iteration + ", currentError=" + fbest + ",target=" + maxError);

			// Check for convergence
			if (prevBest <= 1.0) {
				toler = tol;
			} else {
				toler = tol * prevBest;
			}

			// Stop if there is little improvement
			if ((prevBest - fbest) <= toler) {
				if (++convergenceCounter >= 3) {
					break;
				}
			} else {
				convergenceCounter = 0;
			}

			double dot1 = 0;
			double dot2 = 0;
			double dlen = 0;

			dot1 = dot2 = dlen = 0.0;
			double high = 1.e-4;
			for (int i = 0; i < n; i++) {
				base[i] = x[i];
				if (deriv2[i] > high) {
					high = deriv2[i];
				}
				dot1 += direc[i] * g[i]; // Directional first derivative
				dot2 += direc[i] * direc[i] * deriv2[i]; // and second
				dlen += direc[i] * direc[i]; // Length of search vector
			}

			dlen = Math.sqrt(dlen);

			double scale;

			if (Math.abs(dot2) < Encog.DEFAULT_DOUBLE_EQUAL) {
				scale = 0;
			} else {
				scale = dot1 / dot2;
			}
			high = 1.5 / high;
			if (high < 1.e-4) {
				high = 1.e-4;
			}

			if (scale < 0.0) {
				scale = high;
			} else if (scale < 0.1 * high) {
				scale = 0.1 * high;
			} else if (scale > 10.0 * high) {
				scale = 10.0 * high;
			}

			prevBest = fbest;
			globalMinimum.setY2(fbest);

			globalMinimum.findBestRange(0.0, 2.0 * scale, -3, false, maxError,
					network);

			if (globalMinimum.getY2() < maxError) {
				if (globalMinimum.getY2() < fbest) {
					for (int i = 0; i < n; i++) {
						x[i] = base[i] + globalMinimum.getY2() * direc[i];
						if (x[i] < 1.e-10) {
							x[i] = 1.e-10;
						}
					}
					fbest = globalMinimum.getY2();
				} else {
					for (int i = 0; i < n; i++) {
						x[i] = base[i];
					}
				}
				break;
			}

			if (convergenceCounter > 0) {
				fbest = globalMinimum.brentmin(20, maxError, eps, 1.e-7,
						network, globalMinimum.getY2());
			} else {
				fbest = globalMinimum.brentmin(10, maxError, 1.e-6, 1.e-5,
						network, globalMinimum.getY2());
			}

			for (int i = 0; i < n; i++) {
				x[i] = base[i] + globalMinimum.getX2() * direc[i];
				if (x[i] < 1.e-10) {
					x[i] = 1.e-10;
				}
			}

			improvement = (prevBest - fbest) / prevBest;

			if (fbest < maxError) {
				break;
			}

			for (int i = 0; i < n; i++) {
				direc[i] = -direc[i]; // negative gradient
			}

			gam = gamma(n, g, direc);

			if (gam < 0.0) {
				gam = 0.0;
			}

			if (gam > 10.0) {
				gam = 10.0;
			}

			if (improvement < 0.001) {
				++poorCJ;
			} else {
				poorCJ = 0;
			}

			if (poorCJ >= 2) {
				if (gam > 1.0) {
					gam = 1.0;
				}
			}

			if (poorCJ >= 6) {
				poorCJ = 0;
				gam = 0.0;
			}

			findNewDir(n, gam, g, h, direc);

		}

		return fbest;
	}

	/**
	 * Find gamma.
	 * 
	 * @param n
	 *            The number of variables.
	 * @param gam
	 *            The gamma value.
	 * @param g
	 *            The "g" value, used for CJ algorithm.
	 * @param h
	 *            The "h" value, used for CJ algorithm.
	 * @param grad
	 *            The gradients.
	 */
	private void findNewDir(final int n, final double gam, final double[] g,
			final double[] h, final double[] grad) {
		int i;

		for (i = 0; i < n; i++) {
			g[i] = grad[i];
			grad[i] = h[i] = g[i] + gam * h[i];
		}
	}

	/**
	 * Find correction for next iteration.
	 * 
	 * @param n
	 *            The number of variables.
	 * @param g
	 *            The "g" value, used for CJ algorithm.
	 * @param grad
	 *            The gradients.
	 * @return The correction for the next iteration.
	 */
	private double gamma(final int n, final double[] g, final double[] grad) {
		int i;
		double denom, numer;

		numer = denom = 0.0;

		for (i = 0; i < n; i++) {
			denom += g[i] * g[i];
			numer += (grad[i] - g[i]) * grad[i]; // Grad is neg gradient
		}

		if (denom == 0.0) {
			return 0.0;
		} else {
			return numer / denom;
		}
	}
}
