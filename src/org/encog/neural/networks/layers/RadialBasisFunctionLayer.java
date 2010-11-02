/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

package org.encog.neural.networks.layers;

import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.rbf.RadialBasisFunction;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.mathutil.rbf.GaussianFunction;
import org.encog.mathutil.rbf.InverseMultiquadricFunction;
import org.encog.mathutil.rbf.MexicanHatFunction;
import org.encog.mathutil.rbf.MultiquadricFunction;
import org.encog.mathutil.rbf.RBFEnum;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.RadialBasisFunctionLayerPersistor;

/**
 * This layer type makes use of several radial basis function to scale the
 * output from this layer. Each RBF can have a different center, peak, and
 * width. Proper selection of these values will greatly impact the success of
 * the layer. Currently, Encog provides no automated way of determining these
 * values. There is one RBF per neuron.
 * 
 * Radial basis function layers have neither bias nor a regular activation
 * function. Calling any methods that deal with the activation function or bias
 * values will result in an error.
 * 
 * @author jheaton
 * 
 */
public class RadialBasisFunctionLayer extends BasicLayer {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 2779781041654829282L;

	/**
	 * The radial basis functions to use, there should be one for each neuron.
	 */
	private RadialBasisFunction[] radialBasisFunction;

	/**
	 * Default constructor, mainly so the workbench can easily create a default
	 * layer.
	 */
	public RadialBasisFunctionLayer() {
		this(1);
	}

	/**
	 * Construct a radial basis function layer.
	 * 
	 * @param neuronCount
	 *            The neuron count.
	 */
	public RadialBasisFunctionLayer(final int neuronCount) {
		super(new ActivationLinear(), false, neuronCount);
		this.radialBasisFunction = new RadialBasisFunction[neuronCount];
	}

	/**
	 * Compute the values before sending output to the next layer. This function
	 * allows the activation functions to be called.
	 * 
	 * @param pattern
	 *            The incoming Project.
	 * @returns The output from this layer.
	 */
	@Override
	public NeuralData compute(final NeuralData pattern) {
		final NeuralData result = new BasicNeuralData(getNeuronCount());

		for (int i = 0; i < getNeuronCount(); i++) {

			if (this.radialBasisFunction[i] == null) {
				final String str = "Error, must define radial functions for each neuron";
				throw new NeuralNetworkError(str);
			}

			final RadialBasisFunction f = this.radialBasisFunction[i];

			if (pattern.size() != f.getDimensions()) {
				throw new NeuralNetworkError(
						"Inputs must equal the number of dimensions.");
			}

			result.setData(i, f.calculate(pattern.getData()));
		}

		return result;
	}

	/**
	 * Create a persistor for this layer.
	 * 
	 * @return The persistor.
	 */
	@Override
	public Persistor createPersistor() {
		return new RadialBasisFunctionLayerPersistor();
	}

	/**
	 * Get an array of radial basis functions.
	 * 
	 * @return An array of radial basis functions.
	 */
	public RadialBasisFunction[] getRadialBasisFunction() {
		return this.radialBasisFunction;
	}

	/**
	 * Set the RBF components to random values.
	 * @param dimensions The number of dimensions in the network.
	 * @param min The minimum value for the centers, widths and peaks.
	 * @param max The maximum value for the centers, widths and peaks.
	 * @param t The RBF to use.
	 */
	public void randomizeRBFCentersAndWidths(final int dimensions,
			final double min, final double max, final RBFEnum t) {
		final double[] centers = new double[dimensions];
		for (int i = 0; i < centers.length; i++) {
			centers[i] = RangeRandomizer.randomize(min, max);
		}

		this.radialBasisFunction = new RadialBasisFunction[this.getNeuronCount()];
		for (int i = 0; i < getNeuronCount(); i++) {
			setRBFOptions(i, t, centers, RangeRandomizer.randomize(min,
					max), RangeRandomizer.randomize(min,
					max));
		}
	}

	/**
	 * Set the RBF.
	 * 
	 * @param value
	 *            The new RBF
	 */
	public void setRadialBasisFunction(final RadialBasisFunction[] value) {
		this.radialBasisFunction = value;
	}

	/**
	 * Set the RBF.
	 * 
	 * @param rbfIndex
	 *            The RBF to set.
	 * @param rbfType
	 *            The type of RBF to use.
	 * @param center
	 *            The centers.
	 * @param peak
	 *            The peak.
	 * @param width
	 *            The widths.
	 */
	public void setRBFOptions(final int rbfIndex, final RBFEnum rbfType,
			final double[] center, final double peak, final double width) {
		if (rbfType == RBFEnum.Gaussian) {
			this.radialBasisFunction[rbfIndex] = new GaussianFunction(peak,
					center, width);
		} else if (rbfType == RBFEnum.Multiquadric) {
			this.radialBasisFunction[rbfIndex] = new MultiquadricFunction(peak,
					center, width);
		} else if (rbfType == RBFEnum.InverseMultiquadric) {
			this.radialBasisFunction[rbfIndex] = new InverseMultiquadricFunction(
					peak, center, width);
		} else if (rbfType == RBFEnum.MexicanHat) {
			this.radialBasisFunction[rbfIndex] = new MexicanHatFunction(peak,
					center, width);
		}
	}

}
