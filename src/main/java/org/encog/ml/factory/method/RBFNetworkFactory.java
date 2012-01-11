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
import org.encog.mathutil.rbf.RBFEnum;
import org.encog.ml.MLMethod;
import org.encog.ml.factory.parse.ArchitectureLayer;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.rbf.RBFNetwork;
import org.encog.util.ParamsHolder;

/**
 * A factory to create RBF networks.
 */
public class RBFNetworkFactory {
	
	/**
	 * The max layer count.
	 */
	public static final int MAX_LAYERS = 3;
	
	/**
	 * Create a RBF network.
	 * @param architecture THe architecture string to use.
	 * @param input The input count. 
	 * @param output The output count.
	 * @return The RBF network.
	 */
	public final MLMethod create(final String architecture, final int input,
			final int output) {

		final List<String> layers = ArchitectureParse.parseLayers(architecture);
		if (layers.size() != MAX_LAYERS) {
			throw new EncogError(
					"RBF Networks must have exactly three elements, " 
					+ "separated by ->.");
		}

		final ArchitectureLayer inputLayer = ArchitectureParse.parseLayer(
				layers.get(0), input);
		final ArchitectureLayer rbfLayer = ArchitectureParse.parseLayer(
				layers.get(1), -1);
		final ArchitectureLayer outputLayer = ArchitectureParse.parseLayer(
				layers.get(2), output);

		final int inputCount = inputLayer.getCount();
		final int outputCount = outputLayer.getCount();

		RBFEnum t;

		if (rbfLayer.getName().equalsIgnoreCase("Gaussian")) {
			t = RBFEnum.Gaussian;
		} else if (rbfLayer.getName().equalsIgnoreCase("Multiquadric")) {
			t = RBFEnum.Multiquadric;
		} else if (rbfLayer.getName().equalsIgnoreCase("InverseMultiquadric")) {
			t = RBFEnum.InverseMultiquadric;
		} else if (rbfLayer.getName().equalsIgnoreCase("MexicanHat")) {
			t = RBFEnum.MexicanHat;
		} else {
			throw new NeuralNetworkError("Unknown RBF: " + rbfLayer.getName());
		}

		final ParamsHolder holder = new ParamsHolder(rbfLayer.getParams());

		final int rbfCount = holder.getInt("C", true, 0);

		final RBFNetwork result = new RBFNetwork(inputCount, rbfCount,
				outputCount, t);

		return result;
	}
}
