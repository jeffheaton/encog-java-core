/*
 * Encog(tm) Core v2.5 
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

package org.encog.engine.util;


/**
 * Calculate the error of a neural network. Encog currently supports three error
 * calculation modes. See ErrorCalculationMode for more info.
 */
public class ErrorCalculation {

	/**
	 * The overall error.
	 */
	private double globalError;

	/**
	 * The size of a set.
	 */
	private int setSize;

	/**
	 * The current error calculation mode.
	 */
	private static ErrorCalculationMode mode = ErrorCalculationMode.MSE;

	/**
	 * Returns the root mean square error for a complete training set.
	 * 
	 * @return The current error for the neural network.
	 */
	public double calculate() {
		if (this.setSize == 0) {
			return 0;
		}

		switch( ErrorCalculation.getMode() )
		{
			case RMS:
				return this.calculateRMS();
			case MSE:
				return this.calculateMSE();
			case ARCTAN:
				return this.calculateARCTAN();
			default:
				return this.calculateMSE();
		}

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
	 * Calculate the error with ARCTAN.
	 * 
	 * @return The current error for the neural network.
	 */
	public double calculateARCTAN() {
		return calculateMSE();
	}

	/**
	 * Reset the error accumulation to zero.
	 */
	public void reset() {
		this.globalError = 0;
		this.setSize = 0;
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
			
			if( this.mode==ErrorCalculationMode.ARCTAN )
				delta = Math.atan(delta);
			
			this.globalError += delta * delta;
		}

		this.setSize += ideal.length;
	}



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
	 * return The current mode.
	 */

	public static ErrorCalculationMode getMode() {
		return mode;
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
	public static void setMode(ErrorCalculationMode mode) {
		ErrorCalculation.mode = mode;
	}

	/**
	 * Update the error with single values.
	 * @param actual The actual value.
	 * @param ideal The ideal value.
	 */
	public void updateError(double actual, double ideal) {

		double delta = ideal - actual;

		if (this.mode == ErrorCalculationMode.ARCTAN)
			delta = Math.atan(delta);

		this.globalError += delta * delta;

		this.setSize++;

	}

}
