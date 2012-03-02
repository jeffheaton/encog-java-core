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

import org.encog.mathutil.ComplexNumber;
import org.encog.ml.data.MLComplexData;
import org.encog.ml.data.MLData;
import org.encog.util.kmeans.Centroid;

/**
 * This class implements a data object that can hold complex numbers.  It 
 * implements the interface MLData, so it can be used with nearly any Encog 
 * machine learning method.  However, not all Encog machine learning methods 
 * are designed to work with complex numbers.  A Encog machine learning method 
 * that does not support complex numbers will only be dealing with the 
 * real-number portion of the complex number. 
 */
public class BasicMLComplexData implements MLComplexData {

	/**
	 * The data held by this object.
	 */
	private ComplexNumber[] data;

	/**
	 * Construct this object with the specified data.  Use only real numbers.
	 *
	 * @param d
	 *            The data to construct this object with.
	 */
	public BasicMLComplexData(final double[] d) {
		this(d.length);
		System.arraycopy(d, 0, this.data, 0, d.length);
	}

	/**
	 * Construct this object with the specified data. Use complex numbers.
	 *
	 * @param d
	 *            The data to construct this object with.
	 */
	public BasicMLComplexData(final ComplexNumber[] d) {
		this.data = d;
	}

	/**
	 * Construct this object with blank data and a specified size.
	 *
	 * @param size
	 *            The amount of data to store.
	 */
	public BasicMLComplexData(final int size) {
		this.data = new ComplexNumber[size];
	}

	/**
	 * Construct a new BasicMLData object from an existing one. This makes a
	 * copy of an array. If MLData is not complex, then only reals will be 
	 * created.
	 *
	 * @param d
	 *            The object to be copied.
	 */
	public BasicMLComplexData(final MLData d) {
		this(d.size());
		
		if( d instanceof MLComplexData ) {
			MLComplexData c = (MLComplexData)d;
			for (int i = 0; i < d.size(); i++) {
				this.data[i] = new ComplexNumber(c.getComplexData(i));
			}			
		} else {
			for (int i = 0; i < d.size(); i++) {
				this.data[i] = new ComplexNumber(d.getData(i), 0);
			}	
		}
		
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void add(final int index, final double value) {
		this.data[index].plus(new ComplexNumber(value, 0));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void add(final int index, final ComplexNumber value) {
		this.data[index] = this.data[index].plus(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void clear() {
		for (int i = 0; i < this.data.length; i++) {
			this.data[i] = new ComplexNumber(0, 0);
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
		double[] d = new double[this.data.length];
		for (int i = 0; i < d.length; i++) {
			d[i] = this.data[i].getReal();
		}
		return d;
	}

	/**
	 * @return The complex numbers.
	 */
	@Override
	public final ComplexNumber[] getComplexData() {
		return this.data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double getData(final int index) {
		return this.data[index].getReal();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ComplexNumber getComplexData(final int index) {
		return this.data[index];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setData(final double[] theData) {
		for (int i = 0; i < theData.length; i++) {
			this.data[i] = new ComplexNumber(theData[i], 0);
		}
	}

	/**
	 * @param theData Set the complex data array.
	 */
	@Override
	public final void setData(final ComplexNumber[] theData) {
		this.data = theData;
	}

	/**
	 * Set the data at the specified index.  Note, this will only set the
	 * real part of the complex number.
	 * @param index The index to to set.
	 * @param d The numeric value to set.
	 */
	@Override
	public final void setData(final int index, final double d) {
		this.data[index] = new ComplexNumber(d, 0);
	}

	/**
	 * Set a data element to a complex number.
	 * @param index The index to set.
	 * @param d The complex number.
	 */
	@Override
	public final void setData(final int index, final ComplexNumber d) {
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
			builder.append(this.data[i].toString());
		}
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Not supported.
	 * @return Nothing.
	 */
	@Override
	public Centroid<MLData> createCentroid() {
		return null;
	}
}
