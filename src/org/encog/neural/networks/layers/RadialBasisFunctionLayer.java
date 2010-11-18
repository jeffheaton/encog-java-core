/*
 * Encog(tm) Core v2.6 - Java Version
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
 * Contributed to Encog By M.Fletcher and M.Dean University of Cambridge, Dept.
 * of Physics, UK
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
	 * @return The output from this layer.
	 */
	public NeuralData compute(final NeuralData pattern) {
		final NeuralData result = new BasicNeuralData(getNeuronCount());

		for (int i = 0; i < getNeuronCount(); i++) {

			if (this.radialBasisFunction[i] == null) {
				final String str = 
	"Error, must define radial functions for each neuron";

				throw new NeuralNetworkError(str);
			}

			final RadialBasisFunction f = this.radialBasisFunction[i];

			if (pattern.getData().length != f.getDimensions()) {
				throw new NeuralNetworkError(
						"Inputs must equal the number of dimensions.");
			}

			result.setData(i, f.calculate(pattern.getData()));
		}

		return result;
	}

	/**
	 * @return Create a persistor for this layer.
	 */
	@Override
	public Persistor createPersistor() {
		return new RadialBasisFunctionLayerPersistor();
	}

	/**
	 * @return An array of radial basis functions.
	 */
	public RadialBasisFunction[] getRadialBasisFunction() {
		return this.radialBasisFunction;
	}

	/**
	 * Set the RBF components to random values.
	 * 
	 * @param dimensions
	 *            The number of dimensions in the network.
	 * @param min
	 *            Minimum random value.
	 * @param max
	 *            Max random value.
	 * @param t
	 *            The type of RBF to use.
	 */
	public void randomizeRBFCentersAndWidths(final int dimensions,
			final double min, final double max, final RBFEnum t) {
		final double[] centers = new double[dimensions];

		for (int i = 0; i < dimensions; i++) {
			centers[i] = RangeRandomizer.randomize(min, max);
		}

		for (int i = 0; i < getNeuronCount(); i++) {
			setRBFFunction(i, t, centers, RangeRandomizer.randomize(min, max));
		}
	}

	/**
	 * Set the array of radial basis functions.
	 * 
	 * @param v
	 *            An array of radial basis functions.
	 */
	public void setRadialBasisFunction(final RadialBasisFunction[] v) {
		this.radialBasisFunction = v;
	}

	/**
	 * Array containing center position. Row n contains centers for neuron n.
	 * Row n contains x elements for x number of dimensions.
	 * 
	 * @param centers The centers.
	 * @param widths
	 *            Array containing widths. Row n contains widths for neuron n.
	 *            Row n contains x elements for x number of dimensions.
	 * @param t
	 *            The RBF Function to use for this layer.
	 */
	public void setRBFCentersAndWidths(final double[][] centers,
			final double[] widths, final RBFEnum t) {
		for (int i = 0; i < getNeuronCount(); i++) {
			setRBFFunction(i, t, centers[i], widths[i]);
		}
	}

	/**
	 * Equally spaces all hidden neurons within the n dimensional variable
	 * space.
	 * 
	 * @param minPosition
	 *            The minimum position neurons should be centered. Typically 0.
	 * @param maxPosition
	 *            The maximum position neurons should be centered. Typically 1
	 * @param RBFType
	 *            The RBF type to use.
	 * @param dimensions
	 *            The number of dimensions.
	 * @param volumeNeuronRBFWidth
	 *            The neuron width of neurons within the mesh.
	 * @param useWideEdgeRBFs
	 *            Enables wider RBF's around the boundary of the neuron mesh.
	 */
	public void setRBFCentersAndWidthsEqualSpacing(final double minPosition,
			final double maxPosition, final RBFEnum t,
			final int dimensions, final double volumeNeuronRBFWidth,
			final boolean useWideEdgeRBFs) {
		final int totalNumHiddenNeurons = getNeuronCount();

		final double disMinMaxPosition = Math.abs(maxPosition - minPosition);

		// Check to make sure we have the correct number of neurons for the
		// provided dimensions
		final int expectedSideLength = (int) Math.pow(totalNumHiddenNeurons,
				1.0 / dimensions);
		if (expectedSideLength != Math.pow(totalNumHiddenNeurons,
				1.0 / dimensions)) {
			throw new NeuralNetworkError(
					"Total number of RBF neurons must be some integer to the power of 'dimensions'.");
		}

		final double edgeNeuronRBFWidth = 2.5 * volumeNeuronRBFWidth;

		final double[][] centers = new double[totalNumHiddenNeurons][];
		final double[] widths = new double[totalNumHiddenNeurons];

		for (int i = 0; i < totalNumHiddenNeurons; i++) {
			centers[i] = new double[dimensions];

			final int sideLength = expectedSideLength;

			// Evenly distribute the volume neurons.
			int temp = i;

			// First determine the centers
			for (int j = dimensions; j > 0; j--) {
				// i + j * sidelength + k * sidelength ^2 + ... l * sidelength ^
				// n
				// i - neuron number in x direction, i.e. 0,1,2,3
				// j - neuron number in y direction, i.e. 0,1,2,3
				// Following example assumes sidelength of 4
				// e.g Neuron 5 - x position is (int)5/4 * 0.33 = 0.33
				// then take modulus of 5%4 = 1
				// Neuron 5 - y position is (int)1/1 * 0.33 = 0.33
				centers[i][j - 1] = ((int) (temp / Math.pow(sideLength, j - 1)) * (disMinMaxPosition / (sideLength - 1)))
						+ minPosition;
				temp = temp % (int) (Math.pow(sideLength, j - 1));
			}

			// Now set the widths
			boolean contains = false;

			for (int z = 0; z < centers[0].length; z++) {
				if ((centers[i][z] == 1.0) || (centers[i][z] == 0.0)) {
					contains = true;
				}
			}

			if (contains && useWideEdgeRBFs) {
				widths[i] = edgeNeuronRBFWidth;
			} else {
				widths[i] = volumeNeuronRBFWidth;
			}

			// centers[i] = (double)(1 / (double)(neuronCount - 1)) * (double)i;
		}

		setRBFCentersAndWidths(centers, widths, t);
		// SaveOutNeuronCentersAndWeights(centers, widths);
	}

	/**
	 * Set an RBF function.
	 * 
	 * @param index
	 *            The index to set.
	 * @param t
	 *            The function type.
	 * @param centers
	 *            The centers.
	 * @param width
	 *            The width.
	 */
	public void setRBFFunction(final int index, final RBFEnum t,
			final double[] centers, final double width) {
		if (t == RBFEnum.Gaussian) {
			this.radialBasisFunction[index] = new GaussianFunction(0.5,
					centers, width);
		} else if (t == RBFEnum.Multiquadric) {
			this.radialBasisFunction[index] = new MultiquadricFunction(0.5,
					centers, width);
		} else if (t == RBFEnum.InverseMultiquadric) {
			this.radialBasisFunction[index] = new InverseMultiquadricFunction(
					0.5, centers, width);
		}
	}
}
