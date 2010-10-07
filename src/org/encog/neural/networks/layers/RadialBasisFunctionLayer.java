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
	 * The centers.
	 */
	private double[][] center;

	/**
	 * The radaii.
	 */
	private double[] radius;

	/**
	 * Default constructor, mainly so the workbench can easily create a default
	 * layer.
	 */
	public RadialBasisFunctionLayer() {
		this(1, 1);
	}

	/**
	 * Construct a radial basis function layer.
	 * 
	 * @param neuronCount
	 *            The neuron count.
	 * @param dimensions
	 *            The number of neurons in the input layer.
	 */
	public RadialBasisFunctionLayer(final int neuronCount, 
			final int dimensions) {
		super(new ActivationLinear(), false, neuronCount);
		this.center = new double[neuronCount][dimensions];
		this.radius = new double[neuronCount];
	}

	/**
	 * Add a neuron to this layer.
	 */
	public void addNeuron() {
		final double[] newRadius = new double[getNeuronCount() + 1];
		final double[][] newCenter 
			= new double[getNeuronCount() + 1][getDimensions()];

		for (int x = 0; x < this.radius.length; x++) {
			newRadius[x] = this.radius[x];
			for (int y = 0; y < getDimensions(); y++) {
				newCenter[x][y] = this.center[x][y];
			}
		}

		setNeuronCount(getNeuronCount() + 1);

		this.radius = newRadius;
		this.center = newCenter;
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
		final double[] x = pattern.getData();

		for (int i = 0; i < getNeuronCount(); i++) {

			// take the eucl distance
			double sum = 0;
			for (int j = 0; j < getDimensions(); j++) {
				final double v = (x[j] - this.center[i][j]);
				sum += v * v;
			}

			final double norm = Math.sqrt(sum);

			final double output = BoundMath.exp(-this.radius[i] * norm * norm);

			result.setData(i, output);

		}

		return result;
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
	 * @return The centers.
	 */
	public double[][] getCenter() {
		return this.center;
	}

	/**
	 * @return The dimensions.
	 */
	public int getDimensions() {
		return this.center[0].length;
	}

	/**
	 * @return The radii.
	 */
	public double[] getRadius() {
		return this.radius;
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

	/**
	 * Remove the specified neuron.
	 * @param neuron The neuron to remove.
	 */
	public void removeNeuron(final int neuron) {
		final double[] newRadius = new double[getNeuronCount() - 1];
		final double[][] newCenter = new double[getNeuronCount() - 1][getDimensions()];

		for (int x = 0; x < this.radius.length; x++) {
			if (x != neuron) {
				newRadius[x] = this.radius[x];
				for (int y = 0; y < getDimensions(); y++) {
					newCenter[x][y] = this.center[x][y];
				}
			}
		}

		setNeuronCount(getNeuronCount() - 1);

		this.radius = newRadius;
		this.center = newCenter;
	}
}
