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
package org.encog.neural.rbf;

import org.encog.engine.network.flat.FlatNetworkRBF;
import org.encog.engine.network.rbf.RadialBasisFunction;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.mathutil.rbf.GaussianFunction;
import org.encog.mathutil.rbf.InverseMultiquadricFunction;
import org.encog.mathutil.rbf.MultiquadricFunction;
import org.encog.mathutil.rbf.RBFEnum;
import org.encog.ml.MLRegression;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.persist.BasicPersistedObject;

public class RBFNetwork extends BasicPersistedObject implements MLRegression  {
	
	private FlatNetworkRBF flat;	
	private RadialBasisFunction[] rbf;
	
	public RBFNetwork(final int inputCount, 
			final int outputCount, final RadialBasisFunction[] rbf)
	{
		this.flat = new FlatNetworkRBF(inputCount,rbf.length,outputCount,rbf);
		this.rbf = rbf;
	}
	
	public RBFNetwork(final int inputCount, int hiddenCount, 
			final int outputCount, RBFEnum t)
	{
		this.rbf = new RadialBasisFunction[hiddenCount];
		
		// Set the standard RBF neuron width.
		// Literature seems to suggest this is a good default value.
		double volumeNeuronWidth = 2.0 / hiddenCount;
		
		this.setRBFCentersAndWidthsEqualSpacing(-1, 1, t, inputCount, volumeNeuronWidth, false);
				
		this.flat = new FlatNetworkRBF(inputCount,rbf.length,outputCount,this.rbf);
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

		for (int i = 0; i < this.rbf.length; i++) {
			setRBFFunction(i, t, centers, RangeRandomizer.randomize(min, max));
		}
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
		for (int i = 0; i < this.rbf.length; i++) {
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
		final int totalNumHiddenNeurons = this.rbf.length;

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
			this.rbf[index] = new GaussianFunction(0.5,
					centers, width);
		} else if (t == RBFEnum.Multiquadric) {
			this.rbf[index] = new MultiquadricFunction(0.5,
					centers, width);
		} else if (t == RBFEnum.InverseMultiquadric) {
			this.rbf[index] = new InverseMultiquadricFunction(
					0.5, centers, width);
		}
	}


	@Override
	public int getInputCount() {
		return this.flat.getInputCount();
	}


	@Override
	public int getOutputCount() {
		return this.flat.getOutputCount();
	}
	
	public RadialBasisFunction[] getRBF()
	{
		return this.rbf;
	}
	
	public FlatNetworkRBF getFlatRBF()
	{
		return this.flat;
	}


	@Override
	public NeuralData compute(NeuralData input) {
		NeuralData output = new BasicNeuralData(getOutputCount());
		this.flat.compute(input.getData(), output.getData());
		return output;
	}
}
