package org.encog.neural.activation;

import org.encog.neural.NeuralNetworkError;

/**
 * The step activation function is a very simple activation function. It is the
 * activation function that was used by the original perceptron. Using the
 * default parameters it will return 1 if the input is 0 or greater. Otherwise
 * it will return 1.
 * 
 * The center, low and high properties allow you to define how this activation
 * function works. If the input is equal to center or higher the high property
 * value will be returned, otherwise the low property will be returned. ì This
 * activation function does not have a derivative, and can not be used with
 * propagation training, or any other training that requires a derivative.
 * 
 */
public class ActivationStep extends BasicActivationFunction {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 3416782010146745754L;

	/**
	 * The center.
	 */
	private double center = 0.0;
	
	/**
	 * The low value that is returned.
	 */
	private double low = 0.0;
	
	/**
	 * The high value that is returned.
	 */
	private double high = 1.0;

	/**
	 * @return The center.
	 */
	public double getCenter() {
		return center;
	}

	public void setCenter(double center) {
		this.center = center;
	}

	/**
	 * @return The low value.
	 */
	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	/**
	 * @return The high value.
	 */
	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	/**
	 * @return A clone of this object.
	 */
	@Override
	public Object clone() {
		ActivationStep result = new ActivationStep();
		result.setCenter(this.center);
		result.setHigh(this.high);
		result.setLow(this.low);
		return result;
	}

	/**
	 * The activation function.
	 * @param d The array to calculate the activation function for.
	 */
	public void activationFunction(double[] d) {
		for (int i = 0; i < d.length; i++) {
			if (d[i] >= this.center)
				d[i] = this.high;
			else
				d[i] = this.low;
		}

	}

	/**
	 * Throws an error, there is no derivative.
	 * @param d The array to get the derivative.
	 */
	public void derivativeFunction(double[] d) {
		throw new NeuralNetworkError("Can't use the step activation function "
				+ "where a derivative is required.");

	}

	/**
	 * @return Returns false, this activation function has no derivative.
	 */
	public boolean hasDerivative() {
		return false;
	}

}
