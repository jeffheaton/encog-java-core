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
import org.encog.ml.MLMethod;
import org.encog.ml.factory.parse.ArchitectureLayer;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.neural.pattern.SOMPattern;

/**
 * A factory that is used to produce self-organizing maps.
 */
public class SOMFactory {
	/**
	 * Create a SOM.
	 * @param architecture The architecture string.
	 * @param input The input count.
	 * @param output The output count.
	 * @return The newly created SOM.
	 */
	public final MLMethod create(final String architecture, final int input,
			final int output) {

		final List<String> layers = ArchitectureParse.parseLayers(architecture);
		if (layers.size() != 2) {
			throw new EncogError(
					"SOM's must have exactly two elements, separated by ->.");
		}

		final ArchitectureLayer inputLayer = ArchitectureParse.parseLayer(
				layers.get(0), input);
		final ArchitectureLayer outputLayer = ArchitectureParse.parseLayer(
				layers.get(1), output);

		final int inputCount = inputLayer.getCount();
		final int outputCount = outputLayer.getCount();

		final SOMPattern pattern = new SOMPattern();
		pattern.setInputNeurons(inputCount);
		pattern.setOutputNeurons(outputCount);
		return pattern.generate();
	}
}
