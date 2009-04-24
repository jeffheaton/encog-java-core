/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.neural.data.temporal;

import org.encog.neural.activation.ActivationFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class describes one unit of input, or output, to a temporal neural
 * network. Data can be both an input and output. Inputs are used to attempt
 * predict the output.
 * 
 * @author jheaton 
 */
public class TemporalDataDescription {
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The type of data requested.
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
	 * @param activationFunction The activation function.
	 * @param type The type of data.
	 * @param input Used for input?
	 * @param predict Used for prediction?
	 */
	public TemporalDataDescription(final ActivationFunction activationFunction,
			final Type type, final boolean input, final boolean predict) {
		this(activationFunction, 0, 0, type, input, predict);
	}

	/**
	 * Construct a data description with no activation function or range.
	 * @param type The type of data.
	 * @param input Used for input?
	 * @param predict Used for prediction?
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
