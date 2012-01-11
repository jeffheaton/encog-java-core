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
package org.encog.ml.data.sparse;

import java.io.Serializable;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataError;
import org.encog.util.EngineArray;
import org.encog.util.kmeans.Centroid;

public class SparseMLData implements MLData, Serializable, Cloneable {


	private int[] index;
	
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
	public SparseMLData(final double[] d) {
		this(d.length);
		System.arraycopy(d, 0, this.data, 0, d.length);
		for(int i=0;i<d.length;i++) {
			this.index[i] = i;
		}
	}
	
	/**
	 * Construct this object with the specified data.
	 *
	 * @param d
	 *            The data to construct this object with.
	 */
	public SparseMLData(final double[] d, int[] i) {
		this(d.length);
		System.arraycopy(d, 0, this.data, 0, d.length);
		EngineArray.arrayCopy(d,this.data);
		EngineArray.arrayCopy(i,this.index);
	}

	/**
	 * Construct this object with blank data and a specified size.
	 *
	 * @param size
	 *            The amount of data to store.
	 */
	public SparseMLData(final int size) {
		this.data = new double[size];
		this.index = new int[size];
	}

	/**
	 * Construct a new BasicMLData object from an existing one. This makes a
	 * copy of an array.
	 *
	 * @param d
	 *            The object to be copied.
	 */
	public SparseMLData(final MLData d) {
		this(d.size());
		
		if( d instanceof SparseMLData ) {
			SparseMLData source = (SparseMLData)d;
			EngineArray.arrayCopy(source.getSparseData(),this.data);
			EngineArray.arrayCopy(source.getSparseIndex(),this.index);
		} else {
			EngineArray.arrayCopy(d.getData(),this.data);
		}
	}
	
	public int findIndex(int index) {
		for(int i=0;i<this.index.length;i++) {
			if( this.index[i]==index)
				return i;
		}
		
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void add(final int index, final double value) {
		int i = findIndex(index);
		if( i==-1 ) {
			throw new MLDataError("Can't find sparse index: " + index);
		}
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
		return new SparseMLData(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double[] getData() {
		throw new MLDataError("Can't directly access the data array of a SparseMLData object");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double getData(final int index) {
		int i = findIndex(index);
		if( i==-1 ) {
			throw new MLDataError("Can't find sparse index: " + index);
		}
		return this.data[i];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setData(final double[] theData) {
		throw new MLDataError("Can't directly access the data array of a SparseMLData object");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setData(final int index, final double d) {
		int i = findIndex(index);
		if( i==-1 ) {
			throw new MLDataError("Can't find sparse index: " + index);
		}
		this.data[i] = d;
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
			builder.append(this.index[i]);
			builder.append(':');
			builder.append(this.data[i]);
		}
		builder.append("]");
		return builder.toString();
	}
	
	public int[] getSparseIndex() {
		return this.index;
	}
	
	public double[] getSparseData() {
		return this.data;
	}

	@Override
	public Centroid<MLData> createCentroid() {
		return null;
	}
	
	
}
