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
package org.encog.ml.factory.method;

import java.util.List;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.factory.parse.ArchitectureLayer;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.activation.ActivationLinear;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.activation.ActivationTANH;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;

/**
 * A factor to create feedforward networks.
 *
 */
public class FeedforwardFactory {

	/**
	 * Create a feed forward network.
	 * @param architecture The architecture string to use.
	 * @param input The input count.
	 * @param output The output count.
	 * @return The feedforward network.
	 */
	public final MLMethod create(final String architecture, final int input,
			final int output) {
		final BasicNetwork result = new BasicNetwork();
		final List<String> layers = ArchitectureParse.parseLayers(architecture);
		ActivationFunction activation = new ActivationLinear();

		int questionPhase = 0;
		for (final String layerStr : layers) {
			int defaultCount;
			// determine default
			if (questionPhase == 0) {
				defaultCount = input;
			} else {
				defaultCount = output;
			}

			final ArchitectureLayer layer = ArchitectureParse.parseLayer(
					layerStr, defaultCount);
			final boolean bias = layer.isBias();

			String part = layer.getName();
			if (part != null) {
				part = part.trim();
			} else {
				part = "";
			}

			if ("tanh".equalsIgnoreCase(part)) {
				activation = new ActivationTANH();
			} else if ("linear".equalsIgnoreCase(part)) {
				activation = new ActivationLinear();
			} else if ("sigmoid".equalsIgnoreCase(part)) {
				activation = new ActivationSigmoid();
			} else {
				if (layer.isUsedDefault()) {
					questionPhase++;
					if (questionPhase > 2) {
						throw new EncogError("Only two ?'s may be used.");
					}
				}

				if (layer.getCount() == 0) {
					throw new EncogError("Unknown architecture element: "
							+ architecture + ", can't parse: " + part);
				}
				final Layer layer2 = new BasicLayer(activation, bias,
						layer.getCount());
				result.addLayer(layer2);

			}
		}

		result.getStructure().finalizeStructure();
		result.reset();

		return result;
	}

}
