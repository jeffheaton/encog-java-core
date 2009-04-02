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
package org.encog.neural.data.bipolar;

import java.io.Serializable;

import org.encog.matrix.BiPolarUtil;
import org.encog.neural.data.NeuralData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A NeuralData implementation designed to work with bipolar data.
 * Bipolar data contains two values.  True is stored as 1, and false
 * is stored as -1.
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
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct this object with the specified data.
	 * @param d The data to create this object with.
	 */
	public BiPolarNeuralData(final boolean[] d) {
		this.data = new boolean[d.length];
		System.arraycopy(d, 0, this.data, 0, d.length);
	}

	/**
	 * Construct a data object with the specified size.
	 * @param size The size of this data object.
	 */
	public BiPolarNeuralData(final int size) {
		this.data = new boolean[size];
	}

	/**
	 * Get the specified data item as a boolean.
	 * @param i The index to read.
	 * @return The specified data item's value.
	 */
	public boolean getBoolean(final int i) {
		return this.data[i];
	}

	/**
	 * Get the data held by this object as an array of doubles.
	 * @return The data held by this object.
	 */
	public double[] getData() {
		return BiPolarUtil.bipolar2double(this.data);
	}

	/**
	 * Get the data held by the index.
	 * @param index The index to read.
	 * @return Return the data held at the specified index. 
	 */
	public double getData(final int index) {
		return BiPolarUtil.bipolar2double(this.data[index]);
	}

	/**
	 * Store the array.
	 * @param data The data to store.
	 */
	public void setData(final double[] data) {
		this.data = BiPolarUtil.double2bipolar(data);
	}

	/**
	 * Set the specified index of this object as a boolean.  This
	 * value will be converted into bipolar.
	 * @param index The index to set.
	 * @param value The value to set. 
	 */
	public void setData(final int index, final boolean value) {
		this.data[index] = value;
	}

	/**
	 * Set the specified index of this object as a double.  This
	 * value will be converted into bipolar.
	 * @param index The index to set.
	 * @param d The value to set. 
	 */
	public void setData(final int index, final double d) {
		this.data[index] = BiPolarUtil.double2bipolar(d);
	}

	/**
	 * Get the size of this data object.
	 * @return The size of this data object.
	 */
	public int size() {
		return this.data.length;
	}
}
