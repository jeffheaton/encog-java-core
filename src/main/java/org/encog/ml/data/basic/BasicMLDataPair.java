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
package org.encog.ml.data.basic;

import java.io.Serializable;

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.util.Format;
import org.encog.util.kmeans.Centroid;
import org.encog.util.kmeans.CentroidFactory;

/**
 * A basic implementation of the MLDataPair interface. This implementation
 * simply holds and input and ideal MLData object.
 * 
 * For supervised training both input and ideal should be specified.
 * 
 * For unsupervised training the input property should be valid, however the
 * ideal property should contain null.
 * 
 * @author jheaton
 * 
 */
public class BasicMLDataPair implements MLDataPair, Serializable {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = -9068229682273861359L;
	
	/**
	 * The significance.
	 */
	private double significance = 1.0;

	/**
	 * Create a new data pair object of the correct size for the machine
	 * learning method that is being trained. This object will be passed to the
	 * getPair method to allow the data pair objects to be copied to it.
	 * 
	 * @param inputSize
	 *            The size of the input data.
	 * @param idealSize
	 *            The size of the ideal data.
	 * @return A new data pair object.
	 */
	public static MLDataPair createPair(final int inputSize, 
			final int idealSize) {
		MLDataPair result;

		if (idealSize > 0) {
			result = new BasicMLDataPair(new BasicMLData(inputSize),
					new BasicMLData(idealSize));
		} else {
			result = new BasicMLDataPair(new BasicMLData(inputSize));
		}

		return result;
	}

	/**
	 * The the expected output from the machine learning method, or null for
	 * unsupervised training.
	 */
	private final MLData ideal;

	/**
	 * The training input to the machine learning method.
	 */
	private final MLData input;

	/**
	 * Construct the object with only input. If this constructor is used, then
	 * unsupervised training is being used.
	 * 
	 * @param theInput
	 *            The input to the machine learning method.
	 */
	public BasicMLDataPair(final MLData theInput) {
		this.input = theInput;
		this.ideal = null;
	}

	/**
	 * Construct a BasicMLDataPair class with the specified input and ideal
	 * values.
	 * 
	 * @param theInput
	 *            The input to the machine learning method.
	 * @param theIdeal
	 *            The expected results from the machine learning method.
	 */
	public BasicMLDataPair(final MLData theInput, final MLData theIdeal) {
		this.input = theInput;
		this.ideal = theIdeal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MLData getIdeal() {
		return this.ideal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double[] getIdealArray() {
		if (this.ideal == null) {
			return null;
		}
		return this.ideal.getData();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MLData getInput() {
		return this.input;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double[] getInputArray() {
		return this.input.getData();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isSupervised() {
		return this.ideal != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setIdealArray(final double[] data) {
		this.ideal.setData(data);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setInputArray(final double[] data) {
		this.input.setData(data);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		final StringBuilder builder = new StringBuilder("[");
		builder.append(this.getClass().getSimpleName());
		builder.append(":");
		builder.append("Input:");
		builder.append(getInput());
		builder.append("Ideal:");
		builder.append(getIdeal());
		builder.append(",");
		builder.append("Significance:");
		builder.append(Format.formatPercent(this.significance));
		builder.append("]");
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public double getSignificance() {
		return significance;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSignificance(double significance) {
		this.significance = significance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Centroid<MLDataPair> createCentroid() {
		if( !(this.input instanceof BasicMLData) ) {
			throw new EncogError("The input data type of " + this.input.getClass().getSimpleName() + " must be BasicMLData.");
		}
		return new BasicMLDataPairCentroid(this);
	}

	

}
