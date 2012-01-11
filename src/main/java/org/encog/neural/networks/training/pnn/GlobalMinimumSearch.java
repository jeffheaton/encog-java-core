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

/**
 * Search sigma's for a global minimum. First do a rough search, and then use
 * the "Brent Method" to refine the search for an optimal sigma. This class uses
 * the same sigma for each kernel. Multiple sigmas will be introduced in a later
 * step.
 * 
 * Some of the algorithms in this class are based on C++ code from:
 * 
 * Advanced Algorithms for Neural Networks: A C++ Sourcebook by Timothy Masters
 * John Wiley & Sons Inc (Computers); April 3, 1995 ISBN: 0471105880
 */
public class GlobalMinimumSearch {

	/**
	 * The golden section.
	 */
	public static final double CGOLD = 0.3819660;

	/**
	 * A gamma to the left(lower) of the best(middle) gamma.
	 */
	private double x1;

	/**
	 * The value y1 is the error for x1.
	 */
	private double y1;

	/**
	 * The middle(best) gamma.
	 */
	private double x2;

	/**
	 * The value y2 is the error for x2. This is the best(middle) error.
	 */
	private double y2;

	/**
	 * A gamma to the right(higher) of the middle(best) gamma.
	 */
	private double x3;

	/**
	 * The value y3 is the error for x3.
	 */
	private double y3;

	/**
	 * Use the "Brent Method" to find a better minimum.
	 * 
	 * @param maxIterations
	 *            THe maximum number of iterations.
	 * @param maxError
	 *            We can stop if we reach this error.
	 * @param eps
	 *            The approximate machine precision.
	 * @param tol
	 *            Brent's tolerance, must be >= sqrt( eps )
	 * @param network
	 *            The network to obtain the error from.
	 * @param y
	 *            The error at x2.
	 * @return The best error.
	 */
	public final double brentmin(final int maxIterations,
			final double maxError, final double eps, final double tol,
			final CalculationCriteria network, final double y) {
		double prevdist = 0.0;
		double step = 0.0;

		// xBest is the minimum function ordinate thus far.
		// also keep 2nd and 3rd
		double xbest = this.x2;
		double x2ndBest = this.x2;
		double x3rdBest = this.x2;
		// Keep the minimum bracketed between xlow and xhigh.

		// Get the low and high from our previous "crude" search.
		double xlow = this.x1;
		double xhigh = this.x3;

		double fbest = y;
		double fsecbest = y;
		double fthirdbest = y;

		// Main loop.
		// We will go up to the specified number of iterations.
		// Hopefully we will "break out" long before that happens!
		for (int iter = 0; iter < maxIterations; iter++) {

			// Have we reached an acceptable error?
			if (fbest < maxError) {
				break;
			}

			final double xmid = 0.5 * (xlow + xhigh);
			final double tol1 = tol * (Math.abs(xbest) + eps);
			final double tol2 = 2. * tol1;

			// See if xlow is close relative to tol2,
			// Also, that that xbest is near the midpoint.
			if (Math.abs(xbest - xmid) <= (tol2 - 0.5 * (xhigh - xlow))) {
				break;
			}

			// Don't go to close to eps, the machine precision.
			if ((iter >= 2) && ((fthirdbest - fbest) < eps)) {
				break;
			}

			double xrecent = 0;

			// Try parabolic fit, if we moved far enough.
			if (Math.abs(prevdist) > tol1) {
				// Temps holders for the parabolic estimate
				final double t1 = (xbest - x2ndBest) * (fbest - fthirdbest);
				final double t2 = (xbest - x3rdBest) * (fbest - fsecbest);
				final double numer = (xbest - x3rdBest) * t2
						- (xbest - x2ndBest) * t1;
				final double denom = 2. * (t1 - t2);
				final double testdist = prevdist;
				prevdist = step;
				// This is the parabolic estimate to min.
				if (denom != 0.0) {
					step = numer / denom;
				} else {
					// test failed.
					step = 1.e30;
				}

				// If shrinking, and within bounds, then use the parabolic
				// estimate.
				if ((Math.abs(step) < Math.abs(0.5 * testdist))
						&& (step + xbest > xlow) && (step + xbest < xhigh)) {
					xrecent = xbest + step;
					// If very close to known bounds.
					if ((xrecent - xlow < tol2) || (xhigh - xrecent < tol2)) {
						if (xbest < xmid) {
							step = tol1;
						} else {
							step = -tol1;
						}
					}
				} else {
					// Parabolic estimate poor, so use golden section
					prevdist = (xbest >= xmid) ? xlow - xbest : xhigh - xbest;
					step = GlobalMinimumSearch.CGOLD * prevdist;
				}
			} else { // prevdist did not exceed tol1: we did not move far
				// enough
				// to justify a parabolic fit. Use golden section.
				prevdist = (xbest >= xmid) ? xlow - xbest : xhigh - xbest;
				step = .3819660 * prevdist;
			}

			if (Math.abs(step) >= tol1) {
				xrecent = xbest + step; // another trial we must move a
			} else { // decent distance.
				if (step > 0.) {
					xrecent = xbest + tol1;
				} else {
					xrecent = xbest - tol1;
				}
			}

			/*
			 * At long last we have a trial point 'xrecent'. Evaluate the
			 * function.
			 */

			final double frecent = network.calcErrorWithSingleSigma(xrecent);

			if (frecent < 0.0) {
				break;
			}

			if (frecent <= fbest) { // If we improved...
				if (xrecent >= xbest) {
					xlow = xbest; // replacing the appropriate endpoint
				} else {
					xhigh = xbest;
				}
				x3rdBest = x2ndBest; // Update x and f values for best,
				x2ndBest = xbest; // second and third best
				xbest = xrecent;
				fthirdbest = fsecbest;
				fsecbest = fbest;
				fbest = frecent;
			} else { // We did not improve
				if (xrecent < xbest) {
					xlow = xrecent; // replacing the appropriate endpoint
				} else {
					xhigh = xrecent;
				}

				if ((frecent <= fsecbest) || (x2ndBest == xbest)) {
					x3rdBest = x2ndBest;

					x2ndBest = xrecent;
					fthirdbest = fsecbest;
					fsecbest = frecent;
				} else if ((frecent <= fthirdbest) || (x3rdBest == xbest)
						|| (x3rdBest == x2ndBest)) {
					x3rdBest = xrecent;
					fthirdbest = frecent;
				}
			}
		}

		// update the three sigmas.

		this.x1 = xlow;
		this.x2 = xbest;
		this.x3 = xhigh;

		// return the best.
		return fbest;
	}

	/**
	 * Find the best common gamma. Use the same gamma for all kernels. This is a
	 * crude brute-force search. The range found should be refined using the
	 * "Brent Method", also provided in this class.
	 * 
	 * @param low
	 *            The low gamma to begin the search with.
	 * @param high
	 *            The high gamma to end the search with.
	 * @param numberOfPoints
	 *            The number of points between the low and high. Set this value
	 *            to negative to prevent the first point from being calculated.
	 *            If you do set this to negative, set x2 and y2 to the correct
	 *            values.
	 * @param useLog
	 *            Should we progress "logarithmically" from low to high.
	 * @param minError
	 *            We are done if the error is below this.
	 * @param network
	 *            The network to evaluate.
	 */
	public final void findBestRange(final double low, final double high,
			int numberOfPoints, final boolean useLog, final double minError,
			final CalculationCriteria network) {
		int i, ibest;
		double x, y, rate, previous;
		boolean firstPointKnown;

		// if the number of points is negative, then
		// we already know the first point. Don't recalculate it.
		if (numberOfPoints < 0) {
			numberOfPoints = -numberOfPoints;
			firstPointKnown = true;
		} else {
			firstPointKnown = false;
		}

		// Set the rate to go from high to low. We are either advancing
		// logarithmically, or linear.
		if (useLog) {
			rate = Math.exp(Math.log(high / low) / (numberOfPoints - 1));
		} else {
			rate = (high - low) / (numberOfPoints - 1);
		}

		// Start the search at the low.
		x = low;
		previous = 0.0;
		ibest = -1;

		// keep track of if the error is getting worse.
		boolean gettingWorse = false;

		// Try the specified number of points, between high and low.
		for (i = 0; i < numberOfPoints; i++) {

			// Determine the error. If the first point is known, then us y2 as
			// the error.
			if ((i > 0) || !firstPointKnown) {
				y = network.calcErrorWithSingleSigma(x);
			} else {
				y = this.y2;
			}

			// Have we found a new best candidate point?
			if ((i == 0) || (y < this.y2)) {
				// yes, we found a new candidate point!
				ibest = i;
				this.x2 = x;
				this.y2 = y;
				this.y1 = previous; // Function value to its left
				gettingWorse = false; // Flag that min is not yet bounded
			} else if (i == (ibest + 1)) {
				// Things are getting worse!
				// Might be the right neighbor of the best found.
				this.y3 = y;
				gettingWorse = true;
			}

			// Track the left neighbour of the best.
			previous = y;

			// Is this good enough? Might be able to stop early
			if ((this.y2 <= minError) && (ibest > 0) && gettingWorse) {
				break;
			}

			// Decrease the rate either linearly or
			if (useLog) {
				x *= rate;
			} else {
				x += rate;
			}
		}

		/*
		 * At this point we have a minimum (within low,high) at (x2,y2). Compute
		 * x1 and x3, its neighbors. We already know y1 and y3 (unless the
		 * minimum is at an endpoint!).
		 */

		// We have now located a minimum! Yeah!!
		// Lets calculate the neighbors. x1 and x3, which are the sigmas.
		// We should already have y1 and y3 calculated, these are the errors,
		// and are expensive to recalculate.
		if (useLog) {
			this.x1 = this.x2 / rate;
			this.x3 = this.x2 * rate;
		} else {
			this.x1 = this.x2 - rate;
			this.x3 = this.x2 + rate;
		}

		// We are really done at this point. But for "extra credit", we check to
		// see if things were "getting worse".
		//
		// If NOT, and things were getting better, the user probably cropped the
		// gamma range a bit short. After all, it is hard to guess at a good
		// gamma range.
		//
		// To try and get the best common gamma that we can, we will actually
		// slip off the right-hand high-range and search for an even better
		// gamma.

		if (!gettingWorse) {
			// Search as far as needed! (endless loop)
			for (;;) {

				// calculate at y3(the end point)
				this.y3 = network.calcErrorWithSingleSigma(this.x3);

				// If we are not finding anything better, then stop!
				// We are already outside the specified search range.
				if (this.y3 > this.y2) {
					break;
				}
				if ((this.y1 == this.y2) && (this.y2 == this.y3)) {
					break;
				}

				// Shift the points for the new range, as we have
				// extended to the right.
				this.x1 = this.x2;
				this.y1 = this.y2;
				this.x2 = this.x3;
				this.y2 = this.y3;

				// We want to step further each time. We can't search forever,
				// and we are already outside of the area we were supposed to
				// scan.
				rate *= 3.0;
				if (useLog) {
					this.x3 *= rate;
				} else {
					this.x3 += rate;
				}
			}
		}
		// We will also handle one more "bad situation", which results from a
		// bad gamma search range.
		//
		// What if the first gamma was tried, and that was the best it ever got?
		//
		// If this is the case, there MIGHT be better gammas to the left of the
		// search space. Lets try those.
		else if (ibest == 0) {
			// Search as far as needed! (endless loop)
			for (;;) {
				// Calculate at y3(the begin point)
				this.y1 = network.calcErrorWithSingleSigma(this.x1);

				if (this.y1 < 0.0) {
					return;
				}

				// If we are not finding anything better, then stop!
				// We are already outside the specified search range.
				if (this.y1 > this.y2) {
					break;
				}
				if ((this.y1 == this.y2) && (this.y2 == this.y3)) {
					break;
				}

				// Shift the points for the new range, as we have
				// extended to the left.
				this.x3 = this.x2;
				this.y3 = this.y2;
				this.x2 = this.x1;
				this.y2 = this.y1;

				// We want to step further each time. We can't search forever,
				// and we are already outside of the area we were supposed to
				// scan.
				rate *= 3.0;
				if (useLog) {
					this.x1 /= rate;
				} else {
					this.x1 -= rate;
				}
			}
		}
		return;
	}

	/**
	 * @return X1, which is a gamma to the left(lower) of the best(middle)
	 *         gamma.
	 */
	public final double getX1() {
		return this.x1;
	}

	/**
	 * @return X2, which is the middle(best) gamma.
	 */
	public final double getX2() {
		return this.x2;
	}

	/**
	 * @return X3, which is a gamma to the right(higher) of the middle(best)
	 *         gamma.
	 */
	public final double getX3() {
		return this.x3;
	}

	/**
	 * @return Y1, which is the value y1 is the error for x1.
	 */
	public final double getY1() {
		return this.y1;
	}

	/**
	 * @return Y2, which is the value y2 is the error for x2. This is the
	 *         best(middle) error.
	 */
	public final double getY2() {
		return this.y2;
	}

	/**
	 * @return Y3, which is the value y1 is the error for x1.
	 */
	public final double getY3() {
		return this.y3;
	}

	/**
	 * @param x1
	 *            Set X1, which is a gamma to the left(lower) of the
	 *            best(middle) gamma.
	 */
	public final void setX1(final double x1) {
		this.x1 = x1;
	}

	/**
	 * @param x2
	 *            Set X2, which is the middle(best) gamma.
	 */
	public final void setX2(final double x2) {
		this.x2 = x2;
	}

	/**
	 * @param x3
	 *            Set X3, which is a gamma to the right(higher) of the
	 *            middle(best) gamma.
	 */
	public final void setX3(final double x3) {
		this.x3 = x3;
	}

	/**
	 * @param y1
	 *            Set Y1, which is the value y1 is the error for x1.
	 */
	public final void setY1(final double y1) {
		this.y1 = y1;
	}

	/**
	 * @param y2
	 *            Set Y2, which is the value y2 is the error for x2. This is the
	 *            best(middle) error.
	 */
	public final void setY2(final double y2) {
		this.y2 = y2;
	}

	/**
	 * @param y3
	 *            Set Y3, which is the value y3 is the error for x3.
	 */
	public final void setY3(final double y3) {
		this.y3 = y3;
	}

}
