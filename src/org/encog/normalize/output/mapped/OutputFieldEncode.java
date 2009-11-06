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
package org.encog.normalize.output.mapped;

import java.util.ArrayList;
import java.util.List;

import org.encog.normalize.input.InputField;
import org.encog.normalize.output.BasicOutputField;

/**
 * An encoded output field.  This allows ranges of output values to be
 * mapped to specific values.
 */
public class OutputFieldEncode extends BasicOutputField {

	/**
	 * The source field.
	 */
	private final InputField sourceField;
	
	/**
	 * The catch all value, if nothing matches, then use this value.
	 */
	private double catchAll;
	
	/**
	 * The ranges.
	 */
	private final List<MappedRange> ranges = new ArrayList<MappedRange>();

	/**
	 * Construct an encoded field.
	 * @param sourceField The field that this is based on.
	 */
	public OutputFieldEncode(final InputField sourceField) {
		this.sourceField = sourceField;
	}

	/**
	 * Add a ranged mapped to a value.
	 * @param low The low value for the range.
	 * @param high The high value for the range.
	 * @param value The value that the field should produce for this range.
	 */
	public void addRange(final double low, final double high, final double value) {
		final MappedRange range = new MappedRange(low, high, value);
		this.ranges.add(range);
	}

	/**
	 * Calculate the value for this field.
	 * @param subfield Not used.
	 * @return Return the value for the range the input falls within, or return
	 * the catchall if nothing matches.
	 */
	public double calculate(final int subfield) {
		for (final MappedRange range : this.ranges) {
			if (range.inRange(this.sourceField.getCurrentValue())) {
				return range.getValue();
			}
		}

		return this.catchAll;
	}

	/**
	 * @return The catch all value that will be returned if nothing matches.
	 */
	public double getCatchAll() {
		return this.catchAll;
	}

	/**
	 * @return The source field.
	 */
	public InputField getSourceField() {
		return this.sourceField;
	}

	/**
	 * @return Return 1, no subfield supported.
	 */
	public int getSubfieldCount() {
		return 1;
	}

	/**
	 * Not needed for this sort of output field.
	 */
	public void rowInit() {
	}

	/**
	 * Set the catch all value.
	 * @param catchAll The catch all value that is to be returned if none
	 * of the ranges match.
	 */
	public void setCatchAll(final double catchAll) {
		this.catchAll = catchAll;
	}

}
