/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.engine.util;

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
	 * The default error mode for Encog is RMS.
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
	 * @param mode
	 *            The new mode.
	 */
	public static void setMode(final ErrorCalculationMode mode) {
		ErrorCalculation.mode = mode;
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
	public double calculate() {
		if (this.setSize == 0) {
			return 0;
		}

		switch (ErrorCalculation.getMode()) {
		case RMS:
			return calculateRMS();
		case MSE:
			return calculateMSE();
		case ARCTAN:
			return calculateARCTAN();
		default:
			return calculateMSE();
		}

	}

	/**
	 * Calculate the error with ARCTAN.
	 * 
	 * @return The current error for the neural network.
	 */
	public double calculateARCTAN() {
		return calculateMSE();
	}

	/**
	 * Calculate the error with MSE.
	 * 
	 * @return The current error for the neural network.
	 */
	public double calculateMSE() {
		if (this.setSize == 0) {
			return 0;
		}
		final double err = this.globalError / this.setSize;
		return err;

	}

	/**
	 * Calculate the error with RMS.
	 * 
	 * @return The current error for the neural network.
	 */
	public double calculateRMS() {
		if (this.setSize == 0) {
			return 0;
		}
		final double err = Math.sqrt(this.globalError / this.setSize);
		return err;
	}

	/**
	 * Reset the error accumulation to zero.
	 */
	public void reset() {
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
	public void updateError(final double actual, final double ideal) {

		double delta = ideal - actual;

		if (ErrorCalculation.mode == ErrorCalculationMode.ARCTAN) {
			delta = Math.atan(delta);
		}

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
	public void updateError(final double[] actual, final double[] ideal) {
		for (int i = 0; i < actual.length; i++) {
			double delta = ideal[i] - actual[i];

			if (ErrorCalculation.mode == ErrorCalculationMode.ARCTAN) {
				delta = Math.atan(delta);
			}

			this.globalError += delta * delta;
		}

		this.setSize += ideal.length;
	}

}
