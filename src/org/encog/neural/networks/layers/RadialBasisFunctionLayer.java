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
import org.encog.engine.util.BoundMath;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.RadialBasisFunctionLayerPersistor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * The logging object.
	 */
	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(RadialBasisFunctionLayer.class);

	/**
	 * Default constructor, mainly so the workbench can easily create a default
	 * layer.
	 */
	public RadialBasisFunctionLayer() {
		this(1, 1);
	}

	private double[][] center;

	private double[] radius;

	/**
	 * Construct a radial basis function layer.
	 * 
	 * @param neuronCount
	 *            The neuron count.
	 * @param dimensions
	 *            The number of neurons in the input layer.
	 */
	public RadialBasisFunctionLayer(final int neuronCount, final int dimensions) {
		super(new ActivationLinear(), false, neuronCount);
		this.center = new double[neuronCount][dimensions];
		this.radius = new double[neuronCount];
	}

	/**
	 * Compute the values before sending output to the next layer. This function
	 * allows the activation functions to be called.
	 * 
	 * @param pattern
	 *            The incoming Project.
	 * @return The output from this layer.
	 */
	@Override
	public NeuralData compute(final NeuralData pattern) {

		final NeuralData result = new BasicNeuralData(getNeuronCount());
		double[] x = pattern.getData();

		for (int i = 0; i < getNeuronCount(); i++) {

			// take the eucl distance
			double sum = 0;
			for (int j = 0; j < getDimensions(); j++) {
				double v = (x[j] - center[i][j]);
				sum += v * v;
			}

			double norm = Math.sqrt(sum);

			double output = BoundMath.exp(-this.radius[i] * norm * norm);

			result.setData(i, output);

		}

		return result;
	}

	public int getDimensions() {
		return this.center[0].length;
	}

	/**
	 * Create a persistor for this layer.
	 * 
	 * @return The new persistor.
	 */
	@Override
	public Persistor createPersistor() {
		return new RadialBasisFunctionLayerPersistor();
	}

	/**
	 * Set the Gaussian components to random values.
	 * 
	 * @param min
	 *            The minimum value for the center and radius.
	 * @param max
	 *            The maximum value for the center and radius.
	 */
	public void randomizeGaussianCentersAndWidths(final double min,
			final double max) {

		for (int x = 0; x < this.radius.length; x++) {
			this.radius[x] = RangeRandomizer.randomize(min, max);
			for (int y = 0; y < getDimensions(); y++) {
				this.center[x][y] = RangeRandomizer.randomize(min, max);
			}
		}
	}

	public double[] getRadius() {
		return this.radius;
	}

	public double[][] getCenter() {
		return this.center;
	}

	public void removeNeuron(int neuron) {
		double[] newRadius = new double[this.getNeuronCount() - 1];
		double[][] newCenter = new double[this.getNeuronCount() - 1][this
				.getDimensions()];

		for (int x = 0; x < this.radius.length; x++) {
			if (x != neuron) {
				newRadius[x] = this.radius[x];
				for (int y = 0; y < getDimensions(); y++) {
					newCenter[x][y] = this.center[x][y];
				}
			}
		}

		this.setNeuronCount(getNeuronCount() - 1);

		this.radius = newRadius;
		this.center = newCenter;
	}

	public void addNeuron() {
		double[] newRadius = new double[this.getNeuronCount() + 1];
		double[][] newCenter = new double[this.getNeuronCount() + 1][this
				.getDimensions()];

		for (int x = 0; x < this.radius.length; x++) {
			newRadius[x] = this.radius[x];
			for (int y = 0; y < getDimensions(); y++) {
				newCenter[x][y] = this.center[x][y];
			}
		}

		this.setNeuronCount(getNeuronCount() + 1);

		this.radius = newRadius;
		this.center = newCenter;
	}
}
