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

import org.encog.ml.BasicML;
import org.encog.ml.data.MLData;

/**
 * Abstract class to build PNN networks upon.
 */
public abstract class AbstractPNN  extends BasicML {

	/**
	 * Input neuron count.
	 */
	private final int inputCount;

	/**
	 * Output neuron count. 
	 */
	private final int outputCount;

	/**
	 * Kernel type. 
	 */
	private final PNNKernelType kernel;

	/**
	 *  Output mode.
	 */
	private final PNNOutputMode outputMode; 

	/**
	 * Is trained. 
	 */
	private boolean trained;

	/**
	 * Network error. (MSE)
	 */
	private double error;

	/**
	 * Confusion work area.
	 */
	private int[] confusion;

	/**
	 * First derivative. 
	 */
	private final double[] deriv;

	/**
	 * Second derivative.
	 */
	private final double[] deriv2;

	/**
	 * Index of a sample to exclude. 
	 */
	private int exclude;

	/**
	 * True, if we are using separate sigmas for each class.
	 */
	private boolean separateClass;

	/**
	 * Constructor.
	 * @param kernel The kernel type to use.
	 * @param outputMode The output mode to use.
	 * @param inputCount The input count.
	 * @param outputCount The output count.
	 */
	public AbstractPNN(final PNNKernelType kernel, final PNNOutputMode outputMode,
			final int inputCount, final int outputCount) {
		this.kernel = kernel;
		this.outputMode = outputMode;
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.trained = false;
		this.error = Double.MIN_VALUE;
		this.confusion = null;
		this.exclude = -1;

		this.deriv = new double[inputCount];
		this.deriv2 = new double[inputCount];

		if (this.outputMode == PNNOutputMode.Classification) {
			this.confusion = new int[this.outputCount + 1];
		}
	}

	/**
	 * Compute the output from the network.
	 * @param input The input to the network.
	 * @return The output from the network.
	 */
	public abstract MLData compute(MLData input);

	/**
	 * @return the deriv
	 */
	public double[] getDeriv() {
		return this.deriv;
	}

	/**
	 * @return the deriv2
	 */
	public double[] getDeriv2() {
		return this.deriv2;
	}

	/**
	 * @return the error
	 */
	public double getError() {
		return this.error;
	}

	/**
	 * @return the exclude
	 */
	public int getExclude() {
		return this.exclude;
	}

	/**
	 * @return the inputCount
	 */
	public int getInputCount() {
		return this.inputCount;
	}

	/**
	 * @return the kernel
	 */
	public PNNKernelType getKernel() {
		return this.kernel;
	}

	/**
	 * @return the outputCount
	 */
	public int getOutputCount() {
		return this.outputCount;
	}

	/**
	 * @return the outputMode
	 */
	public PNNOutputMode getOutputMode() {
		return this.outputMode;
	}

	/**
	 * @return the trained
	 */
	public boolean isTrained() {
		return this.trained;
	}

	/**
	 * Reset the confusion.
	 */
	public void resetConfusion() {

	}

	/**
	 * @param error
	 *            the error to set
	 */
	public final void setError(final double error) {
		this.error = error;
	}

	/**
	 * @param exclude
	 *            the exclude to set
	 */
	public final void setExclude(final int exclude) {
		this.exclude = exclude;
	}

	/**
	 * @param trained
	 *            the trained to set
	 */
	public final void setTrained(final boolean trained) {
		this.trained = trained;
	}

	
	/**
	 * @return the separateClass
	 */
	public final boolean isSeparateClass() {
		return separateClass;
	}

	/**
	 * @param separateClass the separateClass to set
	 */
	public final void setSeparateClass(boolean separateClass) {
		this.separateClass = separateClass;
	}
	
}
