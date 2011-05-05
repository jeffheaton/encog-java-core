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
	public final void calculateGradient(final double[] gradients,
			final double[] layerOutput, final double[] weights,
			final double[] layerDelta, final ActivationFunction af,
			final int index, final int fromLayerIndex, final int fromLayerSize,
			final int toLayerIndex, final int toLayerSize) {
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

			layerDelta[yi] = sum * af.derivativeFunction(layerOutput[yi]);
			yi++;
		}
	}

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

	/**
	 * Not used for this type of plugin.
	 * @return Not used.
	 */
	@Override
	public final int getLogLevel() {
		return 0;
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

	/**
	 * Note used for this type of plug in.
	 * 
	 * @param level
	 *            Not used.
	 * @param message
	 *            Not used.
	 */
	@Override
	public void log(final int level, final String message) {

	}

	/**
	 * Note used for this type of plug in.
	 * 
	 * @param level
	 *            Not used.
	 * @param t
	 *            Not used.
	 */
	@Override
	public void log(final int level, final Throwable t) {
	}
}
