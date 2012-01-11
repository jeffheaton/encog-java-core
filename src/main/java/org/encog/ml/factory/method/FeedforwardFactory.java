/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.MLMethod;
import org.encog.ml.factory.MLActivationFactory;
import org.encog.ml.factory.parse.ArchitectureLayer;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

/**
 * A factor to create feedforward networks.
 *
 */
public class FeedforwardFactory {

	/**
	 * Error.
	 */
	public static final String CANT_DEFINE_ACT 
		= "Can't define activation function before first layer.";

	/**
	 * The activation function factory to use.
	 */
	private MLActivationFactory factory = new MLActivationFactory();
	
	/**
	 * Create a feed forward network.
	 * @param architecture The architecture string to use.
	 * @param input The input count.
	 * @param output The output count.
	 * @return The feedforward network.
	 */
	public final MLMethod create(final String architecture, final int input,
			final int output) {
		
		if( input<=0 ) {
			throw new EncogError("Must have at least one input for feedforward.");
		}
		
		if( output<=0 ) {
			throw new EncogError("Must have at least one output for feedforward.");
		}
		
		
		final BasicNetwork result = new BasicNetwork();
		final List<String> layers = ArchitectureParse.parseLayers(architecture);
		ActivationFunction af = new ActivationLinear();

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
			
			ActivationFunction lookup = this.factory.create(part);
			
			if (lookup!=null) {
				af = lookup;
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

				result.addLayer(new BasicLayer(af, bias,
						layer.getCount()));

			}
		}

		result.getStructure().finalizeStructure();
		result.reset();

		return result;
	}

}
