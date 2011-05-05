package org.encog.plugin;

import org.encog.engine.network.activation.ActivationFunction;

public interface EncogPluginType1 extends EncogPluginBase {
	
	public static int SERVICE_TYPE_GENERAL = 0;
	public static int SERVICE_TYPE_LOGGING = 1;
	public static int SERVICE_TYPE_CALCULATION = 2;
		
	/**
	 * This allows the plugin to replace Encog's system layer calculation. This
	 * allows this calculation to be performed by a GPU or perhaps a compiled
	 * C++ application, or some other high-speed means.
	 * 
	 * @param weights The flat network's weights.
	 * @param layerOutput The layer output.
	 * @param startIndex The starting index.
	 * @param outputIndex The output index.
	 * @param outputSize The size of the output layer.
	 * @param inputIndex The input index.
	 * @param inputSize The size of the input layer.
	 * @return The updated index value.
	 */
	int calculateLayer(double[] weights, double[] layerOutput, int startIndex,
			int outputIndex, int outputSize, int inputIndex, int inputSize);
	
	/**
	 * Perform a gradient calculation.
	 * @param gradients The gradients.
	 * @param layerOutput The layer output.
	 * @param weights The weights.
	 * @param layerDelta The layer deltas.
	 * @param af THe activation function.
	 * @param index THhe current index.
	 * @param fromLayerIndex The from layer index.
	 * @param fromLayerSize THe from layer size.
	 * @param toLayerIndex The to layer index.
	 * @param toLayerSize The to layer size.
	 */
	void calculateGradient(
			double[] gradients, 
			double[] layerOutput, 
			double[] weights,
			double[] layerDelta,
			ActivationFunction af,
			int index,
			int fromLayerIndex,
			int fromLayerSize,
			int toLayerIndex,
			int toLayerSize);
	
	int getLogLevel();
	
	void log(int level, String message);
	
	void log(int level, Throwable t);
	
}
