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
package org.encog.util.normalize.output.mapped;

import java.util.ArrayList;
import java.util.List;

import org.encog.util.normalize.input.InputField;
import org.encog.util.normalize.output.BasicOutputField;

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
	public void addRange(final double low, final double high, 
			final double value) {
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
