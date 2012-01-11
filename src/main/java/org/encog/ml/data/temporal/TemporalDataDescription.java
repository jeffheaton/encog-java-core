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
package org.encog.ml.data.temporal;

import org.encog.engine.network.activation.ActivationFunction;

/**
 * This class describes one unit of input, or output, to a temporal neural
 * network. Data can be both an input and output. Inputs are used to attempt
 * predict the output.
 * 
 * @author jheaton
 */
public class TemporalDataDescription {

	/**
	 * The type of data requested.
	 * 
	 * @author jheaton
	 * 
	 */
	public enum Type {
		/**
		 * Data in its raw, unmodified form.
		 */
		RAW,
		/**
		 * The percent change.
		 */
		PERCENT_CHANGE,
		/**
		 * The difference change.
		 */
		DELTA_CHANGE,
	}

	/**
	 * The lowest allowed number.
	 */
	private final double low;

	/**
	 * The highest allowed number.
	 */
	private final double high;

	/**
	 * Is this data item used for input to prediction?
	 */
	private final boolean input;

	/**
	 * Should this data item be predicted?
	 */
	private final boolean predict;

	/**
	 * What type of data is requested?
	 */
	private final Type type;

	/**
	 * What is the index of this data item in relation to the others.
	 */
	private int index;

	/**
	 * Should an activation function be used?
	 */
	private final ActivationFunction activationFunction;

	/**
	 * Construct a data description item. Set both low and high to zero for
	 * unbounded.
	 * 
	 * @param activationFunction
	 *            What activation function should be used?
	 * @param low
	 *            What is the lowest allowed value.
	 * @param high
	 *            What is the highest allowed value.
	 * @param type
	 *            What type of data is this.
	 * @param input
	 *            Used for input?
	 * @param predict
	 *            Used for prediction?
	 */
	public TemporalDataDescription(final ActivationFunction activationFunction,
			final double low, final double high, final Type type,
			final boolean input, final boolean predict) {
		this.low = low;
		this.type = type;
		this.high = high;
		this.input = input;
		this.predict = predict;
		this.activationFunction = activationFunction;
	}

	/**
	 * Construct a data description with an activation function, but no range.
	 * 
	 * @param activationFunction
	 *            The activation function.
	 * @param type
	 *            The type of data.
	 * @param input
	 *            Used for input?
	 * @param predict
	 *            Used for prediction?
	 */
	public TemporalDataDescription(final ActivationFunction activationFunction,
			final Type type, final boolean input, final boolean predict) {
		this(activationFunction, 0, 0, type, input, predict);
	}

	/**
	 * Construct a data description with no activation function or range.
	 * 
	 * @param type
	 *            The type of data.
	 * @param input
	 *            Used for input?
	 * @param predict
	 *            Used for prediction?
	 */
	public TemporalDataDescription(final Type type, final boolean input,
			final boolean predict) {
		this(null, 0, 0, type, input, predict);
	}

	/**
	 * @return the activationFunction
	 */
	public ActivationFunction getActivationFunction() {
		return this.activationFunction;
	}

	/**
	 * @return the high
	 */
	public double getHigh() {
		return this.high;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return this.index;
	}

	/**
	 * @return the low
	 */
	public double getLow() {
		return this.low;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return this.type;
	}

	/**
	 * @return the input
	 */
	public boolean isInput() {
		return this.input;
	}

	/**
	 * @return the predict
	 */
	public boolean isPredict() {
		return this.predict;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(final int index) {
		this.index = index;
	}

}
