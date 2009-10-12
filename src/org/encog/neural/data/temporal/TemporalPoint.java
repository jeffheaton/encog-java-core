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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
	 * Compare two temporal points.
	 * 
	 * @param that
	 *            The other temporal point to compare.
	 * @return Returns 0 if they are equal, less than 0 if this point is less,
	 *         greater than zero if this point is greater.
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
	 * Convert this point to string form.
	 * 
	 * @return This point as a string.
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
