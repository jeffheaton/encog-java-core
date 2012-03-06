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
package org.encog.neural.pnn;

import org.encog.mathutil.EncogMath;
import org.encog.ml.MLClassification;
import org.encog.ml.MLError;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.NeuralNetworkError;
import org.encog.util.EngineArray;
import org.encog.util.simple.EncogUtility;

/**
 * This class implements either a:
 * 
 * Probabilistic Neural Network (PNN)
 * 
 * General Regression Neural Network (GRNN)
 * 
 * To use a PNN specify an output mode of classification, to make use of a GRNN
 * specify either an output mode of regression or un-supervised autoassociation.
 * 
 * The PNN/GRNN networks are potentially very useful. They share some
 * similarities with RBF-neural networks and also the Support Vector Machine
 * (SVM). These network types directly support the use of classification.
 * 
 * The following book was very helpful in implementing PNN/GRNN's in Encog.
 * 
 * Advanced Algorithms for Neural Networks: A C++ Sourcebook
 * 
 * by Timothy Masters, PhD (http://www.timothymasters.info/) John Wiley & Sons
 * Inc (Computers); April 3, 1995, ISBN: 0471105880
 */
public class BasicPNN extends AbstractPNN implements MLRegression, MLError, MLClassification {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7990707837655024635L;

	/**
	 * The sigma's specify the widths of each kernel used.
	 */
	private final double[] sigma;

	/**
	 * The training samples that form the memory of this network.
	 */
	private BasicMLDataSet samples;

	/**
	 * Used for classification, the number of cases in each class.
	 */
	private int[] countPer;

	/**
	 * The prior probability weights.
	 */
	private double[] priors;

	/**
	 * Construct a BasicPNN network.
	 * 
	 * @param kernel
	 *            The kernel to use.
	 * @param outmodel
	 *            The output model for this network.
	 * @param inputCount
	 *            The number of inputs in this network.
	 * @param outputCount
	 *            The number of outputs in this network.
	 */
	public BasicPNN(final PNNKernelType kernel, final PNNOutputMode outmodel,
			final int inputCount, final int outputCount) {
		super(kernel, outmodel, inputCount, outputCount);

		setSeparateClass(false);

		this.sigma = new double[inputCount];
	}

	/**
	 * Compute the output from this network.
	 * 
	 * @param input
	 *            The input to the network.
	 * @return The output from the network.
	 */
	@Override
	public final MLData compute(final MLData input) {

		final double[] out = new double[getOutputCount()];

		double psum = 0.0;

		int r = -1;
		for (final MLDataPair pair : this.samples) {
			r++;

			if (r == getExclude()) {
				continue;
			}

			double dist = 0.0;
			for (int i = 0; i < getInputCount(); i++) {
				double diff = input.getData(i) - pair.getInput().getData(i);
				diff /= this.sigma[i];
				dist += diff * diff;
			}

			if (getKernel() == PNNKernelType.Gaussian) {
				dist = Math.exp(-dist);
			} else if (getKernel() == PNNKernelType.Reciprocal) {
				dist = 1.0 / (1.0 + dist);
			}

			if (dist < 1.e-40) {
				dist = 1.e-40;
			}

			if (getOutputMode() == PNNOutputMode.Classification) {
				final int pop = (int) pair.getIdeal().getData(0);
				out[pop] += dist;
			} else if (getOutputMode() == PNNOutputMode.Unsupervised) {
				for (int i = 0; i < getInputCount(); i++) {
					out[i] += dist * pair.getInput().getData(i);
				}
				psum += dist;
			} else if (getOutputMode() == PNNOutputMode.Regression) {

				for (int i = 0; i < getOutputCount(); i++) {
					out[i] += dist * pair.getIdeal().getData(i);
				}

				psum += dist;
			}
		}

		if (getOutputMode() == PNNOutputMode.Classification) {
			psum = 0.0;
			for (int i = 0; i < getOutputCount(); i++) {
				if (this.priors[i] >= 0.0) {
					out[i] *= this.priors[i] / this.countPer[i];
				}
				psum += out[i];
			}

			if (psum < 1.e-40) {
				psum = 1.e-40;
			}

			for (int i = 0; i < getOutputCount(); i++) {
				out[i] /= psum;
			}

		} else if (getOutputMode() == PNNOutputMode.Unsupervised) {
			for (int i = 0; i < getInputCount(); i++) {
				out[i] /= psum;
			}
		} else if (getOutputMode() == PNNOutputMode.Regression) {
			for (int i = 0; i < getOutputCount(); i++) {
				out[i] /= psum;
			}
		}

		return new BasicMLData(out);
	}

	/**
	 * @return the countPer
	 */
	public final int[] getCountPer() {
		return this.countPer;
	}

	/**
	 * @return the priors
	 */
	public final double[] getPriors() {
		return this.priors;
	}

	/**
	 * @return the samples
	 */
	public final BasicMLDataSet getSamples() {
		return this.samples;
	}

	/**
	 * @return the sigma
	 */
	public final double[] getSigma() {
		return this.sigma;
	}

	/**
	 * @param samples
	 *            the samples to set
	 */
	public final void setSamples(final BasicMLDataSet samples) {
		this.samples = samples;

		// update counts per
		if (getOutputMode() == PNNOutputMode.Classification) {

			this.countPer = new int[getOutputCount()];
			this.priors = new double[getOutputCount()];

			for (final MLDataPair pair : samples) {
				final int i = (int) pair.getIdeal().getData(0);
				if (i >= this.countPer.length) {
					throw new NeuralNetworkError(
							"Training data contains more classes than neural network has output neurons to hold.");
				}
				this.countPer[i]++;
			}

			for (int i = 0; i < this.priors.length; i++) {
				this.priors[i] = -1;
			}

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateProperties() {
		// unneeded

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateError(MLDataSet data) {
		if (getOutputMode() == PNNOutputMode.Classification) {
			return EncogUtility.calculateClassificationError(this, data);
		} else {
			return EncogUtility.calculateRegressionError(this, data);			
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int classify(MLData input) {
		MLData output = compute(input);
		return EngineArray.maxIndex(output.getData());
	}
}
