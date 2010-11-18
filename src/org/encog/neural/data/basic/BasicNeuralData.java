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
package org.encog.neural.data.basic;

import java.io.Serializable;

import org.encog.neural.data.NeuralData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of the NeuralData interface that stores the data in an
 * array.
 *
 * @author jheaton
 *
 */
public class BasicNeuralData implements NeuralData, Serializable, Cloneable {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -3644304891793584603L;

	/**
	 * The data held by this object.
	 */
	private double[] data;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final transient Logger logger = LoggerFactory.getLogger(this
			.getClass());

	/**
	 * Construct this object with the specified data.
	 *
	 * @param d
	 *            The data to construct this object with.
	 */
	public BasicNeuralData(final double[] d) {
		this(d.length);
		System.arraycopy(d, 0, this.data, 0, d.length);
	}

	/**
	 * Construct this object with blank data and a specified size.
	 *
	 * @param size
	 *            The amount of data to store.
	 */
	public BasicNeuralData(final int size) {
		this.data = new double[size];
	}

	/**
	 * Construct a new BasicNeuralData object from an existing one. This makes a
	 * copy of an array.
	 *
	 * @param d
	 *            The object to be copied.
	 */
	public BasicNeuralData(final NeuralData d) {
		this(d.size());
		System.arraycopy(d.getData(), 0, this.data, 0, d.size());
	}

	/**
	 * Add a value to the specified index.
	 *
	 * @param index
	 *            The index to add to.
	 * @param value
	 *            The value to add.
	 */
	public void add(final int index, final double value) {
		this.data[index] += value;
	}

	/**
	 * Set all data to zero.
	 */
	public void clear() {
		for (int i = 0; i < this.data.length; i++) {
			this.data[i] = 0;
		}
	}

	/**
	 * @return A clone of this object.
	 */
	@Override
	public NeuralData clone() {
		return new BasicNeuralData(this);
	}

	/**
	 * Get the data as an array.
	 *
	 * @return The data held by this object.
	 */
	public double[] getData() {
		return this.data;
	}

	/**
	 * Get a data value at the specified index.
	 *
	 * @param index
	 *            The index to read.
	 * @return The data at the specified index.
	 */
	public double getData(final int index) {
		return this.data[index];
	}

	/**
	 * Set the entire data array.
	 *
	 * @param data
	 *            The data to store.
	 */
	public void setData(final double[] data) {
		this.data = data;
	}

	/**
	 * Set the data element specified by the index.
	 *
	 * @param index
	 *            The data element to set.
	 * @param d
	 *            The new value for the specified data element.
	 */
	public void setData(final int index, final double d) {
		this.data[index] = d;
	}

	/**
	 * Get the number of data elements present.
	 *
	 * @return The number of data elements present.
	 */
	public int size() {
		return this.data.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder("[BasicNeuralData:");
		for (int i = 0; i < this.data.length; i++) {
			if (i != 0) {
				builder.append(',');
			}
			builder.append(this.data[i]);
		}
		builder.append("]");
		return builder.toString();
	}
}
