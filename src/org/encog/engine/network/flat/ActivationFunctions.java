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
			for(int i=xOffset;i<xOffset+xLength;i++)
			x[i] = x[i]*params[paramOffset];
			break;
		case ActivationFunctions.ACTIVATION_TANH:
			for(int i=xOffset;i<xOffset+xLength;i++)
			{
			double z = BoundMath.exp(-params[paramOffset] * x[i]);
			x[i] = (1.0 - z) / (1.0 + z);
			}
			break;
		case ActivationFunctions.ACTIVATION_SIGMOID:
			for(int i=xOffset;i<xOffset+xLength;i++)
				x[i] = 1.0 / (1.0 + BoundMath.exp(-params[paramOffset] * x[i]));
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
			return 1;
		case ActivationFunctions.ACTIVATION_TANH:
			return (params[paramOffset] * (1.0 - x * x));
		case ActivationFunctions.ACTIVATION_SIGMOID:
			return params[paramOffset] * x * ( 1.0 - x);
		default:
			throw new EncogEngineError("Unknown activation type: " + type);
		}
	}	
}
