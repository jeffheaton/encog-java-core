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

package org.encog.normalize.segregate;

import org.encog.persist.annotations.EGAttribute;

/**
 * Specifies a range that might be included or excluded.
 */
public class SegregationRange {

	/**
	 * The low end of this range.
	 */
	@EGAttribute
	private double low;

	/**
	 * The high end of this range.
	 */
	@EGAttribute
	private double high;

	/**
	 * Should this range be included.
	 */
	@EGAttribute
	private boolean include;

	/**
	 * Default constructor for reflection.
	 */
	public SegregationRange() {

	}

	/**
	 * Construct a segregation range.
	 * 
	 * @param low
	 *            The low end of the range.
	 * @param high
	 *            The high end of the range.
	 * @param include
	 *            Specifies if the range should be included.
	 */
	public SegregationRange(final double low, final double high,
			final boolean include) {
		super();
		this.low = low;
		this.high = high;
		this.include = include;
	}

	/**
	 * @return The high end of the range.
	 */
	public double getHigh() {
		return this.high;
	}

	/**
	 * @return The low end of the range.
	 */
	public double getLow() {
		return this.low;
	}

	/**
	 * Is this value within the range.
	 * @param value The value to check.
	 * @return True if the value is within the range.
	 */
	public boolean inRange(final double value) {
		return ((value >= this.low) && (value <= this.high));
	}

	/**
	 * @return True if this range should be included.
	 */
	public boolean isIncluded() {
		return this.include;
	}

}
