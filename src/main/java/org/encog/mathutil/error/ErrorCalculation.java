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
package org.encog.mathutil.error;

/**
 * Calculate the error of a neural network. Encog currently supports three error
 * calculation modes. See ErrorCalculationMode for more info.
 */
public class ErrorCalculation {

	/**
	 * The current error calculation mode.
	 */
	private static ErrorCalculationMode mode = ErrorCalculationMode.MSE;

	/**
	 * get the error calculation mode, this is static and therefore global to
	 * all Enocg training. If a particular training method only supports a
	 * particular error calculation method, it may override this value. It will
	 * not change the value set here, rather the training will occur with its
	 * preferred training method. Currently the only training method that does
	 * this is Levenberg Marquardt (LMA).
	 * 
	 * The default error mode for Encog is MSE.
	 * 
	 * @return The current mode.
	 */
	public static ErrorCalculationMode getMode() {
		return ErrorCalculation.mode;
	}

	/**
	 * Set the error calculation mode, this is static and therefore global to
	 * all Enocg training. If a particular training method only supports a
	 * particular error calculation method, it may override this value. It will
	 * not change the value set here, rather the training will occur with its
	 * preferred training method. Currently the only training method that does
	 * this is Levenberg Marquardt (LMA).
	 * 
	 * @param theMode
	 *            The new mode.
	 */
	public static void setMode(final ErrorCalculationMode theMode) {
		ErrorCalculation.mode = theMode;
	}

	/**
	 * The overall error.
	 */
	private double globalError;

	/**
	 * The size of a set.
	 */
	private int setSize;

	/**
	 * Returns the root mean square error for a complete training set.
	 * 
	 * @return The current error for the neural network.
	 */
	public final double calculate() {
		if (this.setSize == 0) {
			return 0;
		}

		switch (ErrorCalculation.getMode()) {
		case RMS:
			return calculateRMS();
		case MSE:
			return calculateMSE();
		case ESS:
			return calculateESS();
		default:
			return calculateMSE();
		}

	}

	/**
	 * Calculate the error with MSE.
	 * 
	 * @return The current error for the neural network.
	 */
	public final double calculateMSE() {
		if (this.setSize == 0) {
			return 0;
		}
		final double err = this.globalError / this.setSize;
		return err;

	}
	
	/**
	 * Calculate the error with SSE.
	 * 
	 * @return The current error for the neural network.
	 */
	public final double calculateESS() {
		if (this.setSize == 0) {
			return 0;
		}
		final double err = this.globalError / 2;
		return err;

	}

	/**
	 * Calculate the error with RMS.
	 * 
	 * @return The current error for the neural network.
	 */
	public final double calculateRMS() {
		if (this.setSize == 0) {
			return 0;
		}
		final double err = Math.sqrt(this.globalError / this.setSize);
		return err;
	}

	/**
	 * Reset the error accumulation to zero.
	 */
	public final void reset() {
		this.globalError = 0;
		this.setSize = 0;
	}

	/**
	 * Update the error with single values.
	 * 
	 * @param actual
	 *            The actual value.
	 * @param ideal
	 *            The ideal value.
	 */
	public final void updateError(final double actual, final double ideal) {

		double delta = ideal - actual;

		this.globalError += delta * delta;

		this.setSize++;

	}

	/**
	 * Called to update for each number that should be checked.
	 * 
	 * @param actual
	 *            The actual number.
	 * @param ideal
	 *            The ideal number.
	 */
	public final void updateError(final double[] actual, final double[] ideal, final double significance) {
		for (int i = 0; i < actual.length; i++) {
			double delta = (ideal[i] - actual[i]) * significance;

			this.globalError += delta * delta;
		}

		this.setSize += ideal.length;
	}

}
