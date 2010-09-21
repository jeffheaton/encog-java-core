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

package org.encog.neural.data.bipolar;

import java.io.Serializable;

import org.encog.mathutil.matrices.BiPolarUtil;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A NeuralData implementation designed to work with bipolar data. Bipolar data
 * contains two values. True is stored as 1, and false is stored as -1.
 *
 * @author jheaton
 *
 */
public class BiPolarNeuralData implements NeuralData, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -6082894455587612231L;
	/**
	 * The data held by this object.
	 */
	private boolean[] data;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct this object with the specified data.
	 *
	 * @param d
	 *            The data to create this object with.
	 */
	public BiPolarNeuralData(final boolean[] d) {
		this.data = new boolean[d.length];
		System.arraycopy(d, 0, this.data, 0, d.length);
	}

	/**
	 * Construct a data object with the specified size.
	 *
	 * @param size
	 *            The size of this data object.
	 */
	public BiPolarNeuralData(final int size) {
		this.data = new boolean[size];
	}

	/**
	 * This will throw an error, as "add" is not supported for bipolar.
	 *
	 * @param index
	 *            Not used.
	 * @param value
	 *            Not used.
	 */
	public void add(final int index, final double value) {
		throw new NeuralDataError("Add is not supported for bipolar data.");
	}

	/**
	 * Set all data to false.
	 */
	public void clear() {
		for (int i = 0; i < this.data.length; i++) {
			this.data[i] = false;
		}

	}

	/**
	 * @return A cloned copy of this object.
	 */
	@Override
	public NeuralData clone() {
		final BiPolarNeuralData result = new BiPolarNeuralData(size());
		for (int i = 0; i < size(); i++) {
			result.setData(i, getData(i));
		}
		return result;
	}

	/**
	 * Get the specified data item as a boolean.
	 *
	 * @param i
	 *            The index to read.
	 * @return The specified data item's value.
	 */
	public boolean getBoolean(final int i) {
		return this.data[i];
	}

	/**
	 * Get the data held by this object as an array of doubles.
	 *
	 * @return The data held by this object.
	 */
	public double[] getData() {
		return BiPolarUtil.bipolar2double(this.data);
	}

	/**
	 * Get the data held by the index.
	 *
	 * @param index
	 *            The index to read.
	 * @return Return the data held at the specified index.
	 */
	public double getData(final int index) {
		return BiPolarUtil.bipolar2double(this.data[index]);
	}

	/**
	 * Store the array.
	 *
	 * @param data
	 *            The data to store.
	 */
	public void setData(final double[] data) {
		this.data = BiPolarUtil.double2bipolar(data);
	}

	/**
	 * Set the specified index of this object as a boolean. This value will be
	 * converted into bipolar.
	 *
	 * @param index
	 *            The index to set.
	 * @param value
	 *            The value to set.
	 */
	public void setData(final int index, final boolean value) {
		this.data[index] = value;
	}

	/**
	 * Set the specified index of this object as a double. This value will be
	 * converted into bipolar.
	 *
	 * @param index
	 *            The index to set.
	 * @param d
	 *            The value to set.
	 */
	public void setData(final int index, final double d) {
		this.data[index] = BiPolarUtil.double2bipolar(d);
	}

	/**
	 * Get the size of this data object.
	 *
	 * @return The size of this data object.
	 */
	public int size() {
		return this.data.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append('[');
		for (int i = 0; i < size(); i++) {
			if (this.getData(i) > 0) {
				result.append("T");
			} else {
				result.append("F");
			}
			if (i != size() - 1) {
				result.append(",");
			}
		}
		result.append(']');
		return (result.toString());
	}
}
