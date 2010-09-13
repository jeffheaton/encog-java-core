package org.encog.engine.network.flat;

import org.encog.engine.util.BoundMath;
import org.encog.engine.util.EngineArray;

/**
 * A flat network designed to handle an RBF.
 */
public class FlatNetworkRBF extends FlatNetwork {

	/**
	 * The RBF centers.
	 */
	private double[][] center;

	/**
	 * The RBF radius.
	 */
	private double[] radius;

	/**
	 * Default constructor.
	 */
	public FlatNetworkRBF() {

	}

	/**
	 * Construct an RBF flat network.
	 * 
	 * @param inputCount
	 *            The number of input neurons. (also the number of dimensions)
	 * @param hiddenCount
	 *            The number of hidden neurons.
	 * @param outputCount The number of output neurons.
	 * @param center The centers.
	 * @param radius The radii.
	 */
	public FlatNetworkRBF(final int inputCount, final int hiddenCount,
			final int outputCount, final double[][] center,
			final double[] radius) {
		this.center = EngineArray.arrayCopy(center);
		this.radius = EngineArray.arrayCopy(radius);

		FlatLayer[] layers = new FlatLayer[3];

		double[] slope = new double[1];
		slope[0] = 1.0;

		layers[0] = new FlatLayer(ActivationFunctions.ACTIVATION_LINEAR,
				inputCount, 0.0, slope);
		layers[1] = new FlatLayer(ActivationFunctions.ACTIVATION_LINEAR,
				hiddenCount, 1.0, slope);
		layers[2] = new FlatLayer(ActivationFunctions.ACTIVATION_LINEAR,
				outputCount, 0.0, slope);

		init(layers);
	}

	/**
	 * Clone the network.
	 * 
	 * @return A clone of the network.
	 */
	@Override
	public FlatNetworkRBF clone() {
		final FlatNetworkRBF result = new FlatNetworkRBF();
		cloneFlatNetwork(result);
		result.center = EngineArray.arrayCopy(this.center);
		result.radius = EngineArray.arrayCopy(this.radius);
		return result;
	}

	/**
	 * Calculate the output for the given input.
	 * 
	 * @param x
	 *            The input.
	 * @param output
	 *            Output will be placed here.
	 */
	@Override
	public void compute(final double[] x, final double[] output) {

		int dimensions = this.center[0].length;
		int outputIndex = this.getLayerIndex()[1];

		for (int i = 0; i < this.center.length; i++) {

			// take the eucl distance
			double sum = 0;
			for (int j = 0; j < dimensions; j++) {
				double v = (x[j] - center[i][j]);
				sum += v * v;
			}

			double norm = Math.sqrt(sum);

			double o = BoundMath.exp(-this.radius[i] * norm * norm);

			this.getLayerOutput()[outputIndex + i] = o;

		}

		// now compute the output
		computeLayer(1);
		EngineArray.arrayCopy(this.getLayerOutput(), 0, output, 0, this
				.getOutputCount());
	}

}
