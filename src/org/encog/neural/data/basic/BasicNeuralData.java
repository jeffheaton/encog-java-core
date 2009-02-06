/*
 * Encog Artificial Intelligence Framework v1.x
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
package org.encog.neural.data.basic;

import java.io.Serializable;

import org.encog.neural.data.NeuralData;

/**
 * Basic implementation of the NeuralData interface that stores the
 * data in an array.  
 * @author jheaton
 *
 */
public class BasicNeuralData implements NeuralData, Serializable {
	
	/**
	 * The serial id. 
	 */
	private static final long serialVersionUID = -3644304891793584603L;
	
	/**
	 * The data held by this object.
	 */
	private double[] data;

	/**
	 * Construct this object with the specified data.
	 * @param d The data to construct this object with.
	 */
	public BasicNeuralData(final double[] d) {
		this(d.length);
		System.arraycopy(d, 0, this.data, 0, d.length);
	}

	/**
	 * Construct this object with blank data and a specified size.
	 * @param size The amount of data to store.
	 */
	public BasicNeuralData(final int size) {
		this.data = new double[size];
	}

	/**
	 * Get the data as an array.
	 * @return The data held by this object.
	 */
	public double[] getData() {
		return this.data;
	}

	/**
	 * Get a data value at the specified index.
	 * @param index The index to read.
	 * @return The data at the specified index.
	 */
	public double getData(final int index) {
		return this.data[index];
	}

	/**
	 * Set the entire data array.
	 * @param data The data to store.
	 */
	public void setData(final double[] data) {
		this.data = data;
	}

	/**
	 * Set the data element specified by the index.
	 * @param index The data element to set.
	 * @param d The new value for the specified data element.
	 */
	public void setData(final int index, final double d) {
		this.data[index] = d;
	}

	/**
	 * Get the number of data elements present.
	 * @return The number of data elements present.
	 */
	public int size() {
		return this.data.length;
	}

	/**
	 * Return a string representation of this object.
	 * @return The string form of this object.
	 */
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
