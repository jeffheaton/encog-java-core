package org.encog.plugin.system;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.plugin.EncogPluginType1;

/**
 * This is the system plugin for Encog calculation. It can be replaced with a
 * GPU plugin for better performance.
 * 
 */
public class SystemCalculationPlugin implements EncogPluginType1 {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int calculateLayer(final double[] weights,
			final double[] layerOutput, final int startIndex,
			final int outputIndex, final int outputSize, final int inputIndex,
			final int inputSize) {

		int index = startIndex;
		final int limitX = outputIndex + outputSize;
		final int limitY = inputIndex + inputSize;

		// weight values
		for (int x = outputIndex; x < limitX; x++) {
			double sum = 0;
			for (int y = inputIndex; y < limitY; y++) {
				sum += weights[index++] * layerOutput[y];
			}
			layerOutput[x] = sum;
		}

		return index;
	}
	
	public void calculateGradient(
			double[] gradients, 
			double[] layerOutput, 
			double[] weights,
			double[] layerDelta,
			ActivationFunction af,
			int index,
			int fromLayerIndex,
			int fromLayerSize,
			int toLayerIndex,
			int toLayerSize) {
		int yi = fromLayerIndex;
		for (int y = 0; y < fromLayerSize; y++) {
			final double output = layerOutput[yi];
			double sum = 0;
			int xi = toLayerIndex;
			int wi = index + y;
			for (int x = 0; x < toLayerSize; x++) {
				gradients[wi] += output * layerDelta[xi];
				sum += weights[wi] * layerDelta[xi];
				wi += fromLayerSize;
				xi++;
			}

			layerDelta[yi] = sum
					* af.derivativeFunction(layerOutput[yi]);
			yi++;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPluginDescription() {
		return "This is the system plugin that provides regular Java-based "
				+ "calculation for Encog.";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPluginName() {
		return "HRI-System-Calculation";
	}

	/**
	 * @return Returns the service type for this plugin. This plugin provides
	 *         the system calculation for layers and gradients. Therefore, this
	 *         plugin returns SERVICE_TYPE_CALCULATION.
	 */
	@Override
	public final int getPluginServiceType() {
		return EncogPluginType1.SERVICE_TYPE_CALCULATION;
	}

	/**
	 * @return This is a type-1 plugin.
	 */
	@Override
	public final int getPluginType() {
		return 1;
	}
}
