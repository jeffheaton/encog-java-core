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
package org.encog.engine.network.flat;

import org.encog.engine.EncogEngineError;
import org.encog.engine.util.BoundMath;
import org.encog.engine.util.EngineArray;

/**
 * The activation functions used by the flat networks.
 */
public final class ActivationFunctions {

	/**
	 * The names of all of the params.
	 */
	public static final String[][] PARAM_NAMES = {
	// ACTIVATION_LINEAR - 0
			{ "slope" },
			// ACTIVATION_TANH - 1
			{ "slope" },
			// ACTIVATION_SIGMOID - 2
			{ "slope" },
			// ACTIVATION_SOFTMAX - 3
			{},
			// ACTIVATION_BIPOLAR - 4
			{},
			// ACTIVATION_STEP - 5
			{ "center", "low", "high" },
			// ACTIVATION_RAMP - 6
			{ "thresholdHigh", "thresholdLow", "high", "low" },
			// ACTIVATION_COMPETITIVE - 7
			{ "maxWinners" },
			// ACTIVATION_SIN - 8
			{},
			// ACTIVATION_LOG - 9
			{},
			// ACTIVATION_GAUSSIAN - 10
			{ "center", "peak", "width" } };

	/**
	 * The offset to the parameter that holds the linear slope.
	 */
	public static final int PARAM_LINEAR_SLOPE = 0;

	/**
	 * The offset to the parameter that holds the tanh slope.
	 */
	public static final int PARAM_TANH_SLOPE = 0;

	/**
	 * The offset to the parameter that holds the sigmoid slope.
	 */
	public static final int PARAM_SIGMOID_SLOPE = 0;

	/**
	 * The offset to the parameter that holds the max winners.
	 */
	public static final int PARAM_COMPETITIVE_MAX_WINNERS = 0;

	/**
	 * The step center parameter.
	 */
	public static final int PARAM_STEP_CENTER = 0;

	/**
	 * The step low parameter.
	 */
	public static final int PARAM_STEP_LOW = 1;

	/**
	 * The step high parameter.
	 */
	public static final int PARAM_STEP_HIGH = 2;

	/**
	 * The ramp high threshold parameter.
	 */
	public static final int PARAM_RAMP_HIGH_THRESHOLD = 0;

	/**
	 * The ramp low threshold parameter.
	 */
	public static final int PARAM_RAMP_LOW_THRESHOLD = 1;

	/**
	 * The ramp high parameter.
	 */
	public static final int PARAM_RAMP_HIGH = 2;

	/**
	 * The ramp low parameter.
	 */
	public static final int PARAM_RAMP_LOW = 3;

	/**
	 * The offset to the parameter that holds the width.
	 */
	public static final int PARAM_GAUSSIAN_CENTER = 0;

	/**
	 * The offset to the parameter that holds the peak.
	 */
	public static final int PARAM_GAUSSIAN_PEAK = 1;

	/**
	 * The offset to the parameter that holds the width.
	 */
	public static final int PARAM_GAUSSIAN_WIDTH = 2;

	/**
	 * A linear activation function.
	 */
	public static final int ACTIVATION_LINEAR = 0;

	/**
	 * A TANH activation function.
	 */
	public static final int ACTIVATION_TANH = 1;

	/**
	 * A sigmoid activation function.
	 */
	public static final int ACTIVATION_SIGMOID = 2;

	/**
	 * A Soft Max activation function.
	 */
	public static final int ACTIVATION_SOFTMAX = 3;

	/**
	 * A Bipolar activation function.
	 */
	public static final int ACTIVATION_BIPOLAR = 4;

	/**
	 * A Step activation function.
	 */
	public static final int ACTIVATION_STEP = 5;

	/**
	 * A Ramp activation function.
	 */
	public static final int ACTIVATION_RAMP = 6;

	/**
	 * A Competitive activation function.
	 */
	public static final int ACTIVATION_COMPETITIVE = 7;

	/**
	 * A Bipolar activation function.
	 */
	public static final int ACTIVATION_SIN = 8;

	/**
	 * A Log activation function.
	 */
	public static final int ACTIVATION_LOG = 9;

	/**
	 * A Gaussian activation function.
	 */
	public static final int ACTIVATION_GAUSSIAN = 10;

	/**
	 * Calculate an activation.
	 * 
	 * @param type
	 *            The type of activation.
	 * @param x
	 *            The input and output array. Input values are provided and the
	 *            array is modified to reflect the output.
	 * @param params
	 *            THe parameters needed for the calculation.
	 * @param xOffset
	 *            The offset into X for where we are calculating.
	 * @param xLength
	 *            The length of the array we are calculating.
	 * @param paramOffset
	 *            The parameter offset.
	 */
	public static void calculateActivation(final int type, final double[] x,
			final double[] params, final int xOffset, final int xLength,
			final int paramOffset) {
		switch (type) {
		case ActivationFunctions.ACTIVATION_LINEAR:
			ActivationFunctions.calculateActivationLINEAR(x, params, xOffset,
					xLength, paramOffset);
			break;
		case ActivationFunctions.ACTIVATION_TANH:
			ActivationFunctions.calculateActivationTANH(x, params, xOffset,
					xLength, paramOffset);
			break;
		case ActivationFunctions.ACTIVATION_SIGMOID:
			ActivationFunctions.calculateActivationSIGMOID(x, params, xOffset,
					xLength, paramOffset);
			break;
		case ACTIVATION_SOFTMAX:
			ActivationFunctions.calculateActivationSOFTMAX(x, params, xOffset,
					xLength, paramOffset);
			break;
		case ACTIVATION_BIPOLAR:
			ActivationFunctions.calculateActivationBIPOLAR(x, params, xOffset,
					xLength, paramOffset);
			break;
		case ACTIVATION_STEP:
			ActivationFunctions.calculateActivationSTEP(x, params, xOffset,
					xLength, paramOffset);
			break;
		case ACTIVATION_RAMP:
			ActivationFunctions.calculateActivationRAMP(x, params, xOffset,
					xLength, paramOffset);
			break;
		case ACTIVATION_COMPETITIVE:
			ActivationFunctions.calculateActivationCOMPETITIVE(x, params,
					xOffset, xLength, paramOffset);
			break;
		case ACTIVATION_SIN:
			ActivationFunctions.calculateActivationSIN(x, params, xOffset,
					xLength, paramOffset);
			break;
		case ACTIVATION_LOG:
			ActivationFunctions.calculateActivationLOG(x, params, xOffset,
					xLength, paramOffset);
			break;
		case ACTIVATION_GAUSSIAN:
			ActivationFunctions.calculateActivationGAUSSIAN(x, params, xOffset,
					xLength, paramOffset);
			break;
		default:
			throw new EncogEngineError("Unknown activation type: " + type);
		}
	}

	/**
	 * Calculate the bipolar activation.
	 *
	 * @param x
	 *            The input and output array. Input values are provided and the
	 *            array is modified to reflect the output.
	 * @param params
	 *            THe parameters needed for the calculation.
	 * @param xOffset
	 *            The offset into X for where we are calculating.
	 * @param xLength
	 *            The length of the array we are calculating.
	 * @param paramOffset
	 *            The parameter offset.
	 */
	public static void calculateActivationBIPOLAR(final double[] x,
			final double[] params, final int xOffset, final int xLength,
			final int paramOffset) {
		for (int i = xOffset; i < xOffset + xLength; i++) {
			if (x[i] > 0) {
				x[i] = 1;
			} else {
				x[i] = -1;
			}
		}
	}

	/**
	 * Calculate the competitive activation.
	 *
	 * @param x
	 *            The input and output array. Input values are provided and the
	 *            array is modified to reflect the output.
	 * @param params
	 *            THe parameters needed for the calculation.
	 * @param xOffset
	 *            The offset into X for where we are calculating.
	 * @param xLength
	 *            The length of the array we are calculating.
	 * @param paramOffset
	 *            The parameter offset.
	 */
	public static void calculateActivationCOMPETITIVE(final double[] x,
			final double[] params, final int xOffset, final int xLength,
			final int paramOffset) {

		final boolean[] winners = new boolean[xLength];
		double sumWinners = 0;

		// find the desired number of winners
		for (int i = 0; i < params[0]; i++) {
			double maxFound = Double.NEGATIVE_INFINITY;
			int winner = -1;

			// find one winner
			for (int j = xOffset; j < xOffset + xLength; j++) {
				if (!winners[j] && (x[j] > maxFound)) {
					winner = j;
					maxFound = x[j];
				}
			}
			sumWinners += maxFound;
			winners[winner] = true;
		}

		// adjust weights for winners and non-winners
		for (int i = xOffset; i < xOffset + xLength; i++) {
			if (winners[i]) {
				x[i] = x[i] / sumWinners;
			} else {
				x[i] = 0.0;
			}
		}
	}


	/**
	 * Calculate the derivative of the activation. It is assumed that the value
	 * x, which is passed to this method, was the output from this activation.
	 * This prevents this method from having to recalculate the activation, just
	 * to recalculate the derivative.
	 * 
	 * @param type
	 *            The type of activation.
	 * @param x
	 *            The activation to calculate for.
	 * @param params
	 *            The parameters for this activation function.
	 * @param paramOffset
	 *            The offset the parameters begin at.
	 * @return The derivative.
	 */
	public static double calculateActivationDerivative(final int type,
			final double x, final double[] params, final int paramOffset) {
		switch (type) {
		case ActivationFunctions.ACTIVATION_LINEAR:
			return ActivationFunctions.calculateActivationDerivativeLINEAR(x,
					params, paramOffset);
		case ActivationFunctions.ACTIVATION_TANH:
			return ActivationFunctions.calculateActivationDerivativeTANH(x,
					params, paramOffset);
		case ActivationFunctions.ACTIVATION_SIGMOID:
			return ActivationFunctions.calculateActivationDerivativeSIGMOID(x,
					params, paramOffset);
		case ACTIVATION_SOFTMAX:
			return ActivationFunctions.calculateActivationDerivativeSOFTMAX(x,
					params, paramOffset);
		case ACTIVATION_BIPOLAR:
			return ActivationFunctions.calculateActivationDerivativeBIPOLAR(x,
					params, paramOffset);
		case ACTIVATION_STEP:
			return ActivationFunctions.calculateActivationDerivativeSTEP(x,
					params, paramOffset);
		case ACTIVATION_RAMP:
			return ActivationFunctions.calculateActivationDerivativeRAMP(x,
					params, paramOffset);
		case ACTIVATION_COMPETITIVE:
			return ActivationFunctions
					.calculateActivationDerivativeCOMPETITIVE(x, params,
							paramOffset);
		case ACTIVATION_SIN:
			return ActivationFunctions.calculateActivationDerivativeSIN(x,
					params, paramOffset);
		case ACTIVATION_LOG:
			return ActivationFunctions.calculateActivationDerivativeLOG(x,
					params, paramOffset);
		case ACTIVATION_GAUSSIAN:
			return ActivationFunctions.calculateActivationDerivativeGAUSSIAN(x,
					params, paramOffset);
		default:
			throw new EncogEngineError("Unknown activation type: " + type);
		}
	}

	/**
	 * Calculate the derivative for bipolar activation. It is assumed that the
	 * value x, which is passed to this method, was the output from this
	 * activation. This prevents this method from having to recalculate the
	 * activation, just to recalculate the derivative.
	 * 
	 * @param x
	 *            The activation to calculate for.
	 * @param params
	 *            The parameters for this activation function.
	 * @param paramOffset
	 *            The offset the parameters begin at.
	 * @return The derivative.
	 */
	public static double calculateActivationDerivativeBIPOLAR(final double x,
			final double[] params, final int paramOffset) {
		return 1;
	}

	/**
	 * Calculate the derivative for competitive activation. It is assumed that
	 * the value x, which is passed to this method, was the output from this
	 * activation. This prevents this method from having to recalculate the
	 * activation, just to recalculate the derivative.
	 * 
	 * @param x
	 *            The activation to calculate for.
	 * @param params
	 *            The parameters for this activation function.
	 * @param paramOffset
	 *            The offset the parameters begin at.
	 * @return The derivative.
	 */
	public static double calculateActivationDerivativeCOMPETITIVE(
			final double x, final double[] params, final int paramOffset) {
		return 1;
	}

	/**
	 * Calculate the derivative for gaussian activation. It is assumed that the
	 * value x, which is passed to this method, was the output from this
	 * activation. This prevents this method from having to recalculate the
	 * activation, just to recalculate the derivative.
	 * 
	 * @param x
	 *            The activation to calculate for.
	 * @param params
	 *            The parameters for this activation function.
	 * @param paramOffset
	 *            The offset the parameters begin at.
	 * @return The derivative.
	 */
	public static double calculateActivationDerivativeGAUSSIAN(final double x,
			final double[] params, final int paramOffset) {
		final double width = params[ActivationFunctions.PARAM_GAUSSIAN_WIDTH];
		final double peak = params[ActivationFunctions.PARAM_GAUSSIAN_PEAK];
		return Math.exp(-0.5 * width * width * x * x) * peak * width * width
				* (width * width * x * x - 1);
	}

	/**
	 * Calculate the derivative for linear activation. It is assumed that the
	 * value x, which is passed to this method, was the output from this
	 * activation. This prevents this method from having to recalculate the
	 * activation, just to recalculate the derivative.
	 * 
	 * @param x
	 *            The activation to calculate for.
	 * @param params
	 *            The parameters for this activation function.
	 * @param paramOffset
	 *            The offset the parameters begin at.
	 * @return The derivative.
	 */
	public static double calculateActivationDerivativeLINEAR(final double x,
			final double[] params, final int paramOffset) {
		return 1;
	}

	/**
	 * Calculate the derivative for log activation. It is assumed that the
	 * value x, which is passed to this method, was the output from this
	 * activation. This prevents this method from having to recalculate the
	 * activation, just to recalculate the derivative.
	 * 
	 * @param x
	 *            The activation to calculate for.
	 * @param params
	 *            The parameters for this activation function.
	 * @param paramOffset
	 *            The offset the parameters begin at.
	 * @return The derivative.
	 */
	public static double calculateActivationDerivativeLOG(final double x,
			final double[] params, final int paramOffset) {
		if (x >= 0) {
			return 1 / (1 + x);
		} else {
			return 1 / (1 - x);
		}
	}

	/**
	 * Calculate the derivative for ramp activation. It is assumed that the
	 * value x, which is passed to this method, was the output from this
	 * activation. This prevents this method from having to recalculate the
	 * activation, just to recalculate the derivative.
	 * 
	 * @param x
	 *            The activation to calculate for.
	 * @param params
	 *            The parameters for this activation function.
	 * @param paramOffset
	 *            The offset the parameters begin at.
	 * @return The derivative.
	 */
	public static double calculateActivationDerivativeRAMP(final double x,
			final double[] params, final int paramOffset) {
		return 1;
	}

	/**
	 * Calculate the derivative for sigmoid activation. It is assumed that the
	 * value x, which is passed to this method, was the output from this
	 * activation. This prevents this method from having to recalculate the
	 * activation, just to recalculate the derivative.
	 * 
	 * @param x
	 *            The activation to calculate for.
	 * @param params
	 *            The parameters for this activation function.
	 * @param paramOffset
	 *            The offset the parameters begin at.
	 * @return The derivative.
	 */
	public static double calculateActivationDerivativeSIGMOID(final double x,
			final double[] params, final int paramOffset) {
		return params[paramOffset] * x * (1.0 - x);
	}

	/**
	 * Calculate the derivative for sin activation. It is assumed that the
	 * value x, which is passed to this method, was the output from this
	 * activation. This prevents this method from having to recalculate the
	 * activation, just to recalculate the derivative.
	 * 
	 * @param x
	 *            The activation to calculate for.
	 * @param params
	 *            The parameters for this activation function.
	 * @param paramOffset
	 *            The offset the parameters begin at.
	 * @return The derivative.
	 */
	public static double calculateActivationDerivativeSIN(final double x,
			final double[] params, final int paramOffset) {
		return BoundMath.cos(x);
	}

	/**
	 * Calculate the derivative for softmax activation. It is assumed that the
	 * value x, which is passed to this method, was the output from this
	 * activation. This prevents this method from having to recalculate the
	 * activation, just to recalculate the derivative.
	 * 
	 * @param x
	 *            The activation to calculate for.
	 * @param params
	 *            The parameters for this activation function.
	 * @param paramOffset
	 *            The offset the parameters begin at.
	 * @return The derivative.
	 */
	public static double calculateActivationDerivativeSOFTMAX(final double x,
			final double[] params, final int paramOffset) {
		return 1;
	}

	/**
	 * Calculate the derivative for step activation. It is assumed that the
	 * value x, which is passed to this method, was the output from this
	 * activation. This prevents this method from having to recalculate the
	 * activation, just to recalculate the derivative.
	 * 
	 * @param x
	 *            The activation to calculate for.
	 * @param params
	 *            The parameters for this activation function.
	 * @param paramOffset
	 *            The offset the parameters begin at.
	 * @return The derivative.
	 */
	public static double calculateActivationDerivativeSTEP(final double x,
			final double[] params, final int paramOffset) {
		return 1;
	}

	/**
	 * Calculate the derivative for tanh activation. It is assumed that the
	 * value x, which is passed to this method, was the output from this
	 * activation. This prevents this method from having to recalculate the
	 * activation, just to recalculate the derivative.
	 * 
	 * @param x
	 *            The activation to calculate for.
	 * @param params
	 *            The parameters for this activation function.
	 * @param paramOffset
	 *            The offset the parameters begin at.
	 * @return The derivative.
	 */
	public static double calculateActivationDerivativeTANH(final double x,
			final double[] params, final int paramOffset) {
		return (params[paramOffset] * (1.0 - x * x));
	}

	
	/**
	 * Calculate the gaussian activation.
	 *
	 * @param x
	 *            The input and output array. Input values are provided and the
	 *            array is modified to reflect the output.
	 * @param params
	 *            THe parameters needed for the calculation.
	 * @param xOffset
	 *            The offset into X for where we are calculating.
	 * @param xLength
	 *            The length of the array we are calculating.
	 * @param paramOffset
	 *            The parameter offset.
	 */
	public static void calculateActivationGAUSSIAN(final double[] x,
			final double[] params, final int xOffset, final int xLength,
			final int paramOffset) {
		for (int i = xOffset; i < xOffset + xLength; i++) {
			x[i] = params[ActivationFunctions.PARAM_GAUSSIAN_PEAK]
			     * BoundMath.exp(-Math.pow(x[i] 
                    - params[ActivationFunctions.PARAM_GAUSSIAN_CENTER],2)
                    / (2.0 * params[ActivationFunctions.PARAM_GAUSSIAN_WIDTH] * params[ActivationFunctions.PARAM_GAUSSIAN_WIDTH]));
		}
	}

	/**
	 * Calculate the linear activation.
	 *
	 * @param x
	 *            The input and output array. Input values are provided and the
	 *            array is modified to reflect the output.
	 * @param params
	 *            THe parameters needed for the calculation.
	 * @param xOffset
	 *            The offset into X for where we are calculating.
	 * @param xLength
	 *            The length of the array we are calculating.
	 * @param paramOffset
	 *            The parameter offset.
	 */
	public static void calculateActivationLINEAR(final double[] x,
			final double[] params, final int xOffset, final int xLength,
			final int paramOffset) {
		for (int i = xOffset; i < xOffset + xLength; i++) {
			x[i] = x[i] * params[paramOffset];
		}
	}

	/**
	 * Calculate the log activation.
	 *
	 * @param x
	 *            The input and output array. Input values are provided and the
	 *            array is modified to reflect the output.
	 * @param params
	 *            THe parameters needed for the calculation.
	 * @param xOffset
	 *            The offset into X for where we are calculating.
	 * @param xLength
	 *            The length of the array we are calculating.
	 * @param paramOffset
	 *            The parameter offset.
	 */
	public static void calculateActivationLOG(final double[] x,
			final double[] params, final int xOffset, final int xLength,
			final int paramOffset) {
		for (int i = xOffset; i < xOffset + xLength; i++) {
			if (x[i] >= 0) {
				x[i] = BoundMath.log(1 + x[i]);
			} else {
				x[i] = -BoundMath.log(1 - x[i]);
			}
		}

	}

	/**
	 * Calculate the ramp activation.
	 *
	 * @param x
	 *            The input and output array. Input values are provided and the
	 *            array is modified to reflect the output.
	 * @param params
	 *            THe parameters needed for the calculation.
	 * @param xOffset
	 *            The offset into X for where we are calculating.
	 * @param xLength
	 *            The length of the array we are calculating.
	 * @param paramOffset
	 *            The parameter offset.
	 */
	public static void calculateActivationRAMP(final double[] x,
			final double[] params, final int xOffset, final int xLength,
			final int paramOffset) {
		final double slope = (params[ActivationFunctions.PARAM_RAMP_HIGH_THRESHOLD] 
           - params[ActivationFunctions.PARAM_RAMP_LOW_THRESHOLD])
				/ (params[ActivationFunctions.PARAM_RAMP_HIGH] - params[ActivationFunctions.PARAM_RAMP_LOW]);

		for (int i = xOffset; i < xOffset + xLength; i++) {
			if (x[i] < params[ActivationFunctions.PARAM_RAMP_LOW_THRESHOLD]) {
				x[i] = params[ActivationFunctions.PARAM_RAMP_LOW];
			} else if (x[i] > params[ActivationFunctions.PARAM_RAMP_HIGH_THRESHOLD]) {
				x[i] = params[ActivationFunctions.PARAM_RAMP_HIGH];
			} else {
				x[i] = (slope * x[i]);
			}
		}
	}

	/**
	 * Calculate the sigmoid activation.
	 *
	 * @param x
	 *            The input and output array. Input values are provided and the
	 *            array is modified to reflect the output.
	 * @param params
	 *            THe parameters needed for the calculation.
	 * @param xOffset
	 *            The offset into X for where we are calculating.
	 * @param xLength
	 *            The length of the array we are calculating.
	 * @param paramOffset
	 *            The parameter offset.
	 */
	public static void calculateActivationSIGMOID(final double[] x,
			final double[] params, final int xOffset, final int xLength,
			final int paramOffset) {
		for (int i = xOffset; i < xOffset + xLength; i++) {
			x[i] = 1.0 / (1.0 + BoundMath.exp(-params[paramOffset] * x[i]));
		}
	}

	/**
	 * Calculate the sin activation.
	 *
	 * @param x
	 *            The input and output array. Input values are provided and the
	 *            array is modified to reflect the output.
	 * @param params
	 *            THe parameters needed for the calculation.
	 * @param xOffset
	 *            The offset into X for where we are calculating.
	 * @param xLength
	 *            The length of the array we are calculating.
	 * @param paramOffset
	 *            The parameter offset.
	 */
	public static void calculateActivationSIN(final double[] x,
			final double[] params, final int xOffset, final int xLength,
			final int paramOffset) {
		for (int i = xOffset; i < xOffset + xLength; i++) {
			x[i] = BoundMath.sin(x[i]);
		}

	}

	/**
	 * Calculate the softmax activation.
	 *
	 * @param x
	 *            The input and output array. Input values are provided and the
	 *            array is modified to reflect the output.
	 * @param params
	 *            THe parameters needed for the calculation.
	 * @param xOffset
	 *            The offset into X for where we are calculating.
	 * @param xLength
	 *            The length of the array we are calculating.
	 * @param paramOffset
	 *            The parameter offset.
	 */
	public static void calculateActivationSOFTMAX(final double[] x,
			final double[] params, final int xOffset, final int xLength,
			final int paramOffset) {

		double sum = 0;
		for (int i = xOffset; i < xOffset + xLength; i++) {
			x[i] = BoundMath.exp(x[i]);
			sum += x[i];
		}
		for (int i = xOffset; i < xOffset + xLength; i++) {
			x[i] = x[i] / sum;
		}
	}

	/**
	 * Calculate the step activation.
	 *
	 * @param x
	 *            The input and output array. Input values are provided and the
	 *            array is modified to reflect the output.
	 * @param params
	 *            THe parameters needed for the calculation.
	 * @param xOffset
	 *            The offset into X for where we are calculating.
	 * @param xLength
	 *            The length of the array we are calculating.
	 * @param paramOffset
	 *            The parameter offset.
	 */
	public static void calculateActivationSTEP(final double[] x,
			final double[] params, final int xOffset, final int xLength,
			final int paramOffset) {

		for (int i = xOffset; i < xOffset + xLength; i++) {
			if (x[i] >= params[ActivationFunctions.PARAM_STEP_CENTER]) {
				x[i] = params[ActivationFunctions.PARAM_STEP_HIGH];
			} else {
				x[i] = params[ActivationFunctions.PARAM_STEP_LOW];
			}
		}
	}

	/**
	 * Calculate the tanh activation.
	 *
	 * @param x
	 *            The input and output array. Input values are provided and the
	 *            array is modified to reflect the output.
	 * @param params
	 *            THe parameters needed for the calculation.
	 * @param xOffset
	 *            The offset into X for where we are calculating.
	 * @param xLength
	 *            The length of the array we are calculating.
	 * @param paramOffset
	 *            The parameter offset.
	 */
	public static void calculateActivationTANH(final double[] x,
			final double[] params, final int xOffset, final int xLength,
			final int paramOffset) {
		for (int i = xOffset; i < xOffset + xLength; i++) {
			final double z = BoundMath.exp(-params[paramOffset] * x[i]);
			x[i] = (1.0 - z) / (1.0 + z);
		}
	}

	/**
	 * Copy parameters.  Accounts for variable sized parameters.
	 * @param source The source.
	 * @param target The target.
	 * @param index The index.
	 * @return The new index.
	 */
	public static int copyParams(final double[] source, final double[] target,
			final int index) {
		EngineArray.arrayCopy(source, 0, target, index, source.length);
		return index + source.length;
	}

	/**
	 * Get the parameter names for an activation function.
	 * @param index The activation function.
	 * @return An array of names.
	 */
	public static String[] getParams(final int index) {
		return ActivationFunctions.PARAM_NAMES[index];
	}

	/**
	 * Private constructor.
	 */
	private ActivationFunctions() {

	}
}
