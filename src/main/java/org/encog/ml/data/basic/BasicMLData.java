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

import org.encog.ml.data.MLData;
import org.encog.util.kmeans.Centroid;

/**
 * Basic implementation of the MLData interface that stores the data in an
 * array.
 *
 * @author jheaton
 *
 */
public class BasicMLData implements MLData, 
	Serializable, Cloneable {

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
	 *
	 * @param d
	 *            The data to construct this object with.
	 */
	public BasicMLData(final double[] d) {
		this(d.length);
		System.arraycopy(d, 0, this.data, 0, d.length);
	}

	/**
	 * Construct this object with blank data and a specified size.
	 *
	 * @param size
	 *            The amount of data to store.
	 */
	public BasicMLData(final int size) {
		this.data = new double[size];
	}

	/**
	 * Construct a new BasicMLData object from an existing one. This makes a
	 * copy of an array.
	 *
	 * @param d
	 *            The object to be copied.
	 */
	public BasicMLData(final MLData d) {
		this(d.size());
		System.arraycopy(d.getData(), 0, this.data, 0, d.size());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void add(final int index, final double value) {
		this.data[index] += value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void clear() {
		for (int i = 0; i < this.data.length; i++) {
			this.data[i] = 0;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MLData clone() {
		return new BasicMLData(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double[] getData() {
		return this.data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double getData(final int index) {
		return this.data[index];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setData(final double[] theData) {
		this.data = theData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setData(final int index, final double d) {
		this.data[index] = d;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int size() {
		return this.data.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		final StringBuilder builder = new StringBuilder("[");
		builder.append(this.getClass().getSimpleName());
		builder.append(":");
		for (int i = 0; i < this.data.length; i++) {
			if (i != 0) {
				builder.append(',');
			}
			builder.append(this.data[i]);
		}
		builder.append("]");
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Centroid<MLData> createCentroid() {
		return new BasicMLDataCentroid(this);
	}
	
	/**
	 * Add one data element to another.  This does not modify the object.
	 * @param o The other data element
	 * @return The result.
	 */
	public MLData plus(MLData o)
	{
		if (size() != o.size())
			throw new IllegalArgumentException();
		
		BasicMLData result = new BasicMLData(size());
		for (int i = 0; i < size(); i++)
			result.setData(i, getData(i) + o.getData(i));
		
		return result;
	}
	
	/**
	 * Multiply one data element with another.  This does not modify the object.
	 * @param o The other data element
	 * @return The result.
	 */
	public MLData times(double d)
	{
		MLData result = new BasicMLData(size());
		
		for (int i = 0; i < size(); i++)
			result.setData(i, getData(i) * d);
		
		return result;
	}
	
	/**
	 * Subtract one data element from another.  This does not modify the object.
	 * @param o The other data element
	 * @return The result.
	 */
	public MLData minus(MLData o)
	{
		if (size() != o.size())
			throw new IllegalArgumentException();
		
		MLData result = new BasicMLData(size());
		for (int i = 0; i < size(); i++)
			result.setData(i,  getData(i) - o.getData(i));
		
		return result;
	}
}
