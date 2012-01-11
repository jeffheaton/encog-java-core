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

/**
 * A temporal point is all of the data captured at one point in time to be used
 * for prediction. One or more data items might be captured at this point. The
 * TemporalDataDescription class is used to describe each of these data items
 * captured at each point.
 *
 * @author jheaton
 */
public class TemporalPoint implements Comparable<TemporalPoint> {

	/**
	 * The sequence number for this point.
	 */
	private int sequence;

	/**
	 * The data for this point.
	 */
	private double[] data;

	/**
	 * Construct a temporal point of the specified size.
	 *
	 * @param size
	 *            The size to create the temporal point for.
	 */
	public TemporalPoint(final int size) {
		this.data = new double[size];
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(final TemporalPoint that) {
		if (getSequence() == that.getSequence()) {
			return 0;
		} else if (getSequence() < that.getSequence()) {
			return -1;
		} else {
			return 1;
		}
	}

	/**
	 * @return the data
	 */
	public double[] getData() {
		return this.data;
	}

	/**
	 * Get the data at the specified index.
	 *
	 * @param index
	 *            The index to get the data at.
	 * @return The data at the specified index.
	 */
	public double getData(final int index) {
		return this.data[index];
	}

	/**
	 * @return The sequence for this point.
	 */
	public int getSequence() {
		return this.sequence;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(final double[] data) {
		this.data = data;
	}

	/**
	 * Set the data at the specified index.
	 *
	 * @param index
	 *            The index to set the data at.
	 * @param d
	 *            The data to set.
	 */
	public void setData(final int index, final double d) {
		this.data[index] = d;
	}

	/**
	 * @param sequence
	 *            the sequence to set
	 */
	public void setSequence(final int sequence) {
		this.sequence = sequence;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder("[TemporalPoint:");
		builder.append("Seq:");
		builder.append(this.sequence);
		builder.append(",Data:");
		for (int i = 0; i < this.data.length; i++) {
			if (i > 0) {
				builder.append(',');
			}
			builder.append(this.data[i]);
		}
		builder.append("]");
		return builder.toString();
	}

}
