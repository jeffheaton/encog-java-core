package org.encog.engine.network.flat;

import org.encog.engine.EncogEngineError;
import org.encog.engine.util.BoundMath;

public class ActivationFunctions {
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
		
	}
	public static void calculateActivationSTEP(
			final int type, 
			final double[] x, 
			final double[] params,
			final int xOffset,
			final int xLength,
			final int paramOffset) {
		
	}
	
	public static void calculateActivationRAMP(
			final int type, 
			final double[] x, 
			final double[] params,
			final int xOffset,
			final int xLength,
			final int paramOffset) {
		
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
		
	}
	
	public static void calculateActivationLOG(
			final int type, 
			final double[] x, 
			final double[] params,
			final int xOffset,
			final int xLength,
			final int paramOffset) {
		
	}
	
	public static void calculateActivationGAUSSIAN(
			final int type, 
			final double[] x, 
			final double[] params,
			final int xOffset,
			final int xLength,
			final int paramOffset) {
		
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
		return 1;
	}
	
	public static double calculateActivationDerivativeLOG(
			final int type, 
			final double x, 
			final double[] params,
			final int paramOffset) {
		return 1;
	}
	
	public static double calculateActivationDerivativeGAUSSIAN(
			final int type, 
			final double x, 
			final double[] params,
			final int paramOffset) {
		return 1;
	}
	
	
	
	
	
	
}
