/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
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
	 * Return a string representation of this object.
	 * 
	 * @return The string form of this object.
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
