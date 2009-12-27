/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
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
package org.encog.normalize.output.mapped;

import org.encog.persist.annotations.EGAttribute;

/**
 * Simple class that is used internally to hold a range mapping.
 */
public class MappedRange {

	/**
	 * The low value for the range.
	 */
	@EGAttribute
	private final double low;
	
	/**
	 * The high value for the range.
	 */
	@EGAttribute
	private final double high;
	
	/**
	 * The value that should be returned for this range.
	 */
	@EGAttribute
	private final double value;

	/**
	 * Construct the range mapping.
	 * @param low The low value for the range.
	 * @param high The high value for the range.
	 * @param value The value that this range represents.
	 */
	public MappedRange(final double low, final double high, final double value) {
		super();
		this.low = low;
		this.high = high;
		this.value = value;
	}

	/**
	 * @return The high value for this range.
	 */	
	public double getHigh() {
		return this.high;
	}

	/**
	 * @return The low value for this range.
	 */
	public double getLow() {
		return this.low;
	}

	/**
	 * @return The value that this range represents.
	 */
	public double getValue() {
		return this.value;
	}

	/**
	 * Determine if the specified value is in the range.
	 * @param d The value to check.
	 * @return True if this value is within the range.
	 */
	public boolean inRange(final double d) {
		if ((d >= this.low) && (d <= this.high)) {
			return true;
		}
		return false;
	}

}
