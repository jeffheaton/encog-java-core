/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.plugin;

import org.encog.engine.network.activation.ActivationFunction;

/**
 * A type-1 plugin. Currently, type-1 is the only type of plugin. This interface
 * should never change, to maximize compatability with future versions.
 * 
 */
public interface EncogPluginType1 extends EncogPluginBase {

	/**
	 * A general plugin, you can have multiple plugins installed that provide
	 * general services.
	 */
	int SERVICE_TYPE_GENERAL = 0;

	/**
	 * A special plugin that provides logging. You may only have one logging
	 * plugin installed.
	 */
	int SERVICE_TYPE_LOGGING = 1;

	/**
	 * A special plugin that provides calculation. You may only have one
	 * calculation plugin installed.
	 */
	int SERVICE_TYPE_CALCULATION = 2;

	/**
	 * This allows the plugin to replace Encog's system layer calculation. This
	 * allows this calculation to be performed by a GPU or perhaps a compiled
	 * C++ application, or some other high-speed means.
	 * 
	 * @param weights
	 *            The flat network's weights.
	 * @param layerOutput
	 *            The layer output.
	 * @param startIndex
	 *            The starting index.
	 * @param outputIndex
	 *            The output index.
	 * @param outputSize
	 *            The size of the output layer.
	 * @param inputIndex
	 *            The input index.
	 * @param inputSize
	 *            The size of the input layer.
	 * @return The updated index value.
	 */
	int calculateLayer(double[] weights, double[] layerOutput, int startIndex,
			int outputIndex, int outputSize, int inputIndex, int inputSize);

	/**
	 * Perform a gradient calculation.
	 * 
	 * @param gradients
	 *            The gradients.
	 * @param layerOutput
	 *            The layer output.
	 * @param weights
	 *            The weights.
	 * @param layerDelta
	 *            The layer deltas.
	 * @param af
	 *            THe activation function.
	 * @param index
	 *            THhe current index.
	 * @param fromLayerIndex
	 *            The from layer index.
	 * @param fromLayerSize
	 *            THe from layer size.
	 * @param toLayerIndex
	 *            The to layer index.
	 * @param toLayerSize
	 *            The to layer size.
	 */
	void calculateGradient(double[] gradients, double[] layerOutput,
			double[] weights, double[] layerDelta, ActivationFunction af,
			int index, int fromLayerIndex, int fromLayerSize, int toLayerIndex,
			int toLayerSize);

	/**
	 * @return The current log level.
	 */
	int getLogLevel();

	/**
	 * Log a message at the specified level.
	 * @param level The level to log at.
	 * @param message The message to log.
	 */
	void log(int level, String message);

	/**
	 * Log a throwable at the specified level.
	 * @param level The level to log at.
	 * @param t The error to log.
	 */
	void log(int level, Throwable t);

}
