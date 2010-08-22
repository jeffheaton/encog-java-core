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
	 * A TANH activation function.
	 */
	public static final int ACTIVATION_CLASSIC_TANH = 3;

	/**
	 * A sigmoid activation function.
	 */
	public static final int ACTIVATION_CLASSIC_SIGMOID = 4;
	
	
	/**
	 * Calculate an activation.
	 * @param type The type of activation.
	 * @param x The value to calculate the activation for.
	 * @return The resulting value.
	 */
	public static double calculateActivation(final int type, final double x, final double slope) {
		switch (type) {
		case ActivationFunctions.ACTIVATION_LINEAR:
			return x*slope;
		case ActivationFunctions.ACTIVATION_TANH:
			return -1.0 + (2 / (1 + BoundMath.exp(-2 * x)));
		case ActivationFunctions.ACTIVATION_SIGMOID:
			return 1.0 / (1 + BoundMath.exp(-1.0 * x));
		case ActivationFunctions.ACTIVATION_CLASSIC_TANH:
			double z = BoundMath.exp(-slope * x);
			return (1d - z) / (1d + z);
		case ActivationFunctions.ACTIVATION_CLASSIC_SIGMOID:
			return 1.0 / (1.0 + BoundMath.exp(-slope * x));
		default:
			throw new EncogEngineError("Unknown activation type: " + type);
		}
	}

	/**
	 * Calculate the derivative of the activation.
	 * @param type The type of activation.
	 * @param x The value to calculate for.
	 * @return The result.
	 */
	public static double calculateActivationDerivative(final int type, final double x, final double slope) {
		double out;
		
		switch (type) {
		case ActivationFunctions.ACTIVATION_LINEAR:
			return 1;
		case ActivationFunctions.ACTIVATION_TANH:
			return (1 + x) * (1 - x);
		case ActivationFunctions.ACTIVATION_SIGMOID:
			out = calculateActivation(type,x,slope);
			return slope * out * (1d - out);
		case ActivationFunctions.ACTIVATION_CLASSIC_TANH:
			out = calculateActivation(type,x,slope);
			return (slope * (1d - out * out));
		case ActivationFunctions.ACTIVATION_CLASSIC_SIGMOID:
			out = calculateActivation(type,x,slope);
			return slope * out * (1d - out);
		default:
			throw new EncogEngineError("Unknown activation type: " + type);
		}
	}

	
}
