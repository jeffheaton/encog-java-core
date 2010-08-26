package org.encog.engine.network.flat;

import org.encog.engine.EncogEngineError;
import org.encog.engine.util.BoundMath;
import org.encog.engine.util.EngineArray;

public class ActivationFunctions {
	
	public static final String[][] PARAM_NAMES = {
		// ACTIVATION_LINEAR - 0
		{ "slope" },
		// ACTIVATION_TANH - 1
		{ "slope" },
		// ACTIVATION_SIGMOID - 2
		{ "slope" },
		// ACTIVATION_SOFTMAX - 3
		{ },
		// ACTIVATION_BIPOLAR - 4
		{ },
		// ACTIVATION_STEP - 5
		{ "center","low","high" },
		// ACTIVATION_RAMP - 6
		{ "thresholdHigh","thresholdLow", "high", "low" },
		// ACTIVATION_COMPETITIVE - 7
		{ "maxWinners" },
		// ACTIVATION_SIN - 8
		{  },
		// ACTIVATION_LOG - 9
		{  },
		// ACTIVATION_GAUSSIAN - 10
		{ "center", "peak", "width" }
	};
	
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
	
	public static final int PARAM_STEP_CENTER = 0;
	public static final int PARAM_STEP_LOW = 1;
	public static final int PARAM_STEP_HIGH = 2;
	
	public static final int PARAM_RAMP_HIGH_THRESHOLD = 0;
	public static final int PARAM_RAMP_LOW_THRESHOLD = 1;
	public static final int PARAM_RAMP_HIGH = 2;
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
	 * @param type The type of activation.
	 * @param x The value to calculate the activation for.
	 * @return The resulting value.
	 */
	public static void calculateActivation(
			final int type, 
			final double[] x, 
			final double[] params,
			final int xOffset,
			final int xLength,
			final int paramOffset) {
		switch (type) {
		case ActivationFunctions.ACTIVATION_LINEAR:
			calculateActivationLINEAR(type,x,params,xOffset,xLength,paramOffset);
			break;
		case ActivationFunctions.ACTIVATION_TANH:
			calculateActivationTANH(type,x,params,xOffset,xLength,paramOffset);
			break;
		case ActivationFunctions.ACTIVATION_SIGMOID:
			calculateActivationSIGMOID(type,x,params,xOffset,xLength,paramOffset);
			break;						
		case ACTIVATION_SOFTMAX:
			calculateActivationSOFTMAX(type,x,params,xOffset,xLength,paramOffset);
			break;			
		case ACTIVATION_BIPOLAR:
			calculateActivationBIPOLAR(type,x,params,xOffset,xLength,paramOffset);
			break;			
		case ACTIVATION_STEP:
			calculateActivationSTEP(type,x,params,xOffset,xLength,paramOffset);
			break;			
		case ACTIVATION_RAMP:
			calculateActivationRAMP(type,x,params,xOffset,xLength,paramOffset);
			break;			
		case ACTIVATION_COMPETITIVE:
			calculateActivationCOMPETITIVE(type,x,params,xOffset,xLength,paramOffset);
			break;			
		case ACTIVATION_SIN:
			calculateActivationSIN(type,x,params,xOffset,xLength,paramOffset);
			break;
		case ACTIVATION_LOG:
			calculateActivationLOG(type,x,params,xOffset,xLength,paramOffset);
			break;			
		case ACTIVATION_GAUSSIAN:
			calculateActivationGAUSSIAN(type,x,params,xOffset,xLength,paramOffset);
			break;			
		default:
			throw new EncogEngineError("Unknown activation type: " + type);
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
	 * @param slope
	 *            If this activation supports a slope, this is the slope of the
	 *            activation function.
	 * @return The result.
	 */
	public static double calculateActivationDerivative(
			final int type, 
			final double x, 
			final double[] params,
			final int paramOffset) {
		switch (type) {
		case ActivationFunctions.ACTIVATION_LINEAR:
			return calculateActivationDerivativeLINEAR(type,x,params,paramOffset);
		case ActivationFunctions.ACTIVATION_TANH:
			return calculateActivationDerivativeTANH(type,x,params,paramOffset);
		case ActivationFunctions.ACTIVATION_SIGMOID:
			return calculateActivationDerivativeSIGMOID(type,x,params,paramOffset);			
		case ACTIVATION_SOFTMAX:
			return calculateActivationDerivativeSOFTMAX(type,x,params,paramOffset);			
		case ACTIVATION_BIPOLAR:
			return calculateActivationDerivativeBIPOLAR(type,x,params,paramOffset);			
		case ACTIVATION_STEP:
			return calculateActivationDerivativeSTEP(type,x,params,paramOffset);			
		case ACTIVATION_RAMP:
			return calculateActivationDerivativeRAMP(type,x,params,paramOffset);			
		case ACTIVATION_COMPETITIVE:
			return calculateActivationDerivativeCOMPETITIVE(type,x,params,paramOffset);			
		case ACTIVATION_SIN:
			return calculateActivationDerivativeSIN(type,x,params,paramOffset);			
		case ACTIVATION_LOG:
			return calculateActivationDerivativeLOG(type,x,params,paramOffset);			
		case ACTIVATION_GAUSSIAN:
			return calculateActivationDerivativeGAUSSIAN(type,x,params,paramOffset);						
		default:
			throw new EncogEngineError("Unknown activation type: " + type);
		}
	}
	
	public static void calculateActivationLINEAR(
			final int type, 
			final double[] x, 
			final double[] params,
			final int xOffset,
			final int xLength,
			final int paramOffset) {
		for(int i=xOffset;i<xOffset+xLength;i++)
			x[i] = x[i]*params[paramOffset];	
	}
	
	public static void calculateActivationTANH(
			final int type, 
			final double[] x, 
			final double[] params,
			final int xOffset,
			final int xLength,
			final int paramOffset) {
		for(int i=xOffset;i<xOffset+xLength;i++)
		{
		double z = BoundMath.exp(-params[paramOffset] * x[i]);
		x[i] = (1.0 - z) / (1.0 + z);
		}	
	}
	
	public static void calculateActivationSIGMOID(
			final int type, 
			final double[] x, 
			final double[] params,
			final int xOffset,
			final int xLength,
			final int paramOffset) {
		for(int i=xOffset;i<xOffset+xLength;i++)
			x[i] = 1.0 / (1.0 + BoundMath.exp(-params[paramOffset] * x[i]));		
	}
	
	public static void calculateActivationSOFTMAX(
			final int type, 
			final double[] x, 
			final double[] params,
			final int xOffset,
			final int xLength,
			final int paramOffset) {
		
		double sum = 0;
		for(int i=xOffset;i<xOffset+xLength;i++) {
			x[i] = BoundMath.exp(x[i]);
			sum += x[i];
		}
		for(int i=xOffset;i<xOffset+xLength;i++) {
			x[i] = x[i] / sum;
		}		
	}
	
	public static void calculateActivationBIPOLAR(
			final int type, 
			final double[] x, 
			final double[] params,
			final int xOffset,
			final int xLength,
			final int paramOffset) {
		for(int i=xOffset;i<xOffset+xLength;i++) {
			if (x[i] > 0) {
				x[i] = 1;
			} else {
				x[i] = -1;
			}
		}
	}
	public static void calculateActivationSTEP(
			final int type, 
			final double[] x, 
			final double[] params,
			final int xOffset,
			final int xLength,
			final int paramOffset) {
		
		for(int i=xOffset;i<xOffset+xLength;i++) {
			if (x[i] >= params[ActivationFunctions.PARAM_STEP_CENTER])
				x[i] = params[ActivationFunctions.PARAM_STEP_HIGH];
			else
				x[i] = params[ActivationFunctions.PARAM_STEP_LOW];
		}
	}
	
	public static void calculateActivationRAMP(
			final int type, 
			final double[] x, 
			final double[] params,
			final int xOffset,
			final int xLength,
			final int paramOffset) {
		double slope = (params[ActivationFunctions.PARAM_RAMP_HIGH_THRESHOLD] - params[ActivationFunctions.PARAM_RAMP_LOW_THRESHOLD])/
		(params[ActivationFunctions.PARAM_RAMP_HIGH] - params[ActivationFunctions.PARAM_RAMP_LOW]);
	
		for(int i=xOffset;i<xOffset+xLength;i++) {
		if (x[i] < params[ActivationFunctions.PARAM_RAMP_LOW_THRESHOLD]) {
			x[i] = params[ActivationFunctions.PARAM_RAMP_LOW];
		} else if (x[i] > params[ActivationFunctions.PARAM_RAMP_HIGH_THRESHOLD]) {
			x[i] = params[ActivationFunctions.PARAM_RAMP_HIGH];
		} else {
			x[i] = (slope * x[i]);
		}
	}
	}
	
	public static void calculateActivationCOMPETITIVE(
			final int type, 
			final double[] x, 
			final double[] params,
			final int xOffset,
			final int xLength,
			final int paramOffset) {
		
		final boolean[] winners = new boolean[xLength];
		double sumWinners = 0;

		// find the desired number of winners
		for (int i = 0; i < params[0]; i++) {
			double maxFound = Double.NEGATIVE_INFINITY;
			int winner = -1;

			// find one winner
			for (int j = xOffset; j < xOffset+xLength; j++) {
				if (!winners[j] && (x[j] > maxFound)) {
					winner = j;
					maxFound = x[j];
				}
			}
			sumWinners += maxFound;
			winners[winner] = true;
		}

		// adjust weights for winners and non-winners
		for (int i = xOffset; i < xOffset+xLength; i++) {
			if (winners[i]) {
				x[i] = x[i] / sumWinners;
			} else {
				x[i] = 0.0;
			}
		}
	}
	
	public static void calculateActivationSIN(
			final int type, 
			final double[] x, 
			final double[] params,
			final int xOffset,
			final int xLength,
			final int paramOffset) {
		for(int i=xOffset;i<xOffset+xLength;i++) {
			x[i] = BoundMath.sin(x[i]);
		}

	}
	
	public static void calculateActivationLOG(
			final int type, 
			final double[] x, 
			final double[] params,
			final int xOffset,
			final int xLength,
			final int paramOffset) {
		for(int i=xOffset;i<xOffset+xLength;i++) {
			if (x[i] >= 0) {
				x[i] = BoundMath.log(1 + x[i]);
			} else {
				x[i] = -BoundMath.log(1 - x[i]);
			}
		}

	}
	
	public static void calculateActivationGAUSSIAN(
			final int type, 
			final double[] x, 
			final double[] params,
			final int xOffset,
			final int xLength,
			final int paramOffset) {
		for(int i=xOffset;i<xOffset+xLength;i++) {
		x[i] = params[ActivationFunctions.PARAM_GAUSSIAN_PEAK]
		* BoundMath.exp(-Math.pow(x[i] - params[ActivationFunctions.PARAM_GAUSSIAN_CENTER], 2)
				/ (2.0 * params[ActivationFunctions.PARAM_GAUSSIAN_WIDTH] * params[ActivationFunctions.PARAM_GAUSSIAN_WIDTH]));
		}
	}	
	
	
	
	
	/////////////////////////
	
	
	
	
	public static double calculateActivationDerivativeLINEAR(
			final int type, 
			final double x, 
			final double[] params,
			final int paramOffset) {
		return 1;
	}

	
	public static double calculateActivationDerivativeTANH(
			final int type, 
			final double x, 
			final double[] params,
			final int paramOffset) {
		return (params[paramOffset] * (1.0 - x * x));				
	}
	
	public static double calculateActivationDerivativeSIGMOID(
			final int type, 
			final double x, 
			final double[] params,
			final int paramOffset) {
		return params[paramOffset] * x * ( 1.0 - x);
	}
	
	public static double calculateActivationDerivativeSOFTMAX(
			final int type, 
			final double x, 
			final double[] params,
			final int paramOffset) {
		return 1;
	}
	
	public static double calculateActivationDerivativeBIPOLAR(
			final int type, 
			final double x, 
			final double[] params,
			final int paramOffset) {
		return 1;
	}
	public static double calculateActivationDerivativeSTEP(
			final int type, 
			final double x, 
			final double[] params,
			final int paramOffset) {
		return 1;
	}
	
	public static double calculateActivationDerivativeRAMP(
			final int type, 
			final double x, 
			final double[] params,
			final int paramOffset) {
		return 1;
	}
	
	public static double calculateActivationDerivativeCOMPETITIVE(
			final int type, 
			final double x, 
			final double[] params,
			final int paramOffset) {
		return 1;
	}
	
	public static double calculateActivationDerivativeSIN(
			final int type, 
			final double x, 
			final double[] params,
			final int paramOffset) {
		return BoundMath.cos(x);
	}
	
	public static double calculateActivationDerivativeLOG(
			final int type, 
			final double x, 
			final double[] params,
			final int paramOffset) {
		if (x >= 0) {
			return 1 / (1 + x);
		} else {
			return 1 / (1 - x);
		}
	}
	
	public static double calculateActivationDerivativeGAUSSIAN(
			final int type, 
			final double x, 
			final double[] params,
			final int paramOffset) {
		double width = params[ActivationFunctions.PARAM_GAUSSIAN_WIDTH];
		double peak = params[ActivationFunctions.PARAM_GAUSSIAN_PEAK];
		return Math.exp(-0.5 * width * width * x * x) * peak
		* width * width
		* (width * width * x * x - 1);
	}
	
	public static String[] getParams(int index)
	{
		return PARAM_NAMES[index];
	}

	public static int copyParams(double[] source, double[] target, int index) {
		EngineArray.arrayCopy(source, 0, target, index, source.length);
		return index+source.length;
	}
}
