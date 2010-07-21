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

package org.encog.engine.util;

/**
 * A range of integers.
 */
public class IntRange {

	/**
	 * The low end of the range.
	 */
	private int high;

	/**
	 * The high end of the range.
	 */
	private int low;

	/**
	 * Construct an integer range.
	 * 
	 * @param high
	 *            The high end of the range.
	 * @param low
	 *            The low end of the range.
	 */
	public IntRange(final int high, final int low) {
		super();
		this.high = high;
		this.low = low;
	}

	/**
	 * @return The high end of the range.
	 */
	public int getHigh() {
		return this.high;
	}

	/**
	 * @return The low end of the range.
	 */
	public int getLow() {
		return this.low;
	}

	/**
	 * Set the high end of the range.
	 * 
	 * @param high
	 *            The high end of the range.
	 */
	public void setHigh(final int high) {
		this.high = high;
	}

	/**
	 * Set the low end of the range.
	 * 
	 * @param low
	 *            The low end of the range.
	 */
	public void setLow(final int low) {
		this.low = low;
	}

}
