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
package org.encog.util.normalize.output;

import org.encog.util.normalize.input.InputField;

/**
 * A ranged mapped output field. This will scale the input so that it is between
 * the high and low value.
 */
public class OutputFieldRangeMapped extends BasicOutputField implements
		RequireTwoPass {

	/**
	 * Calculate a ranged mapped value. 
	 * @param value The to map.
	 * @param min The minimum that the value param can be.
	 * @param max The maximum that the value param can be.
	 * @param hi The high value to map into.
	 * @param lo The low value to map into.
	 * @return The mapped value.
	 */
	public static double calculate(final double value, final double min,
			final double max, final double hi, final double lo) {
		return ((value - min) / (max - min)) * (hi - lo) + lo;
	}

	/**
	 * The input field to scale.
	 */
	private InputField field;

	/**
	 * The low value of the field.
	 */
	private double low;

	/**
	 * The high value of the field.
	 */
	private double high;

	/**
	 * Default constructor, used mainly for reflection.
	 */
	public OutputFieldRangeMapped() {

	}

	/**
	 * Construct a range mapped output field.
	 * 
	 * @param field
	 *            The input field to base this on.
	 * @param low
	 *            The low value.
	 * @param high
	 *            The high value.
	 */
	public OutputFieldRangeMapped(final InputField field, final double low,
			final double high) {
		this.field = field;
		this.low = low;
		this.high = high;
	}

	/**
	 * Create a range field with -1 and 1 as low/high.
	 * @param f The input field to use.
	 */
	public OutputFieldRangeMapped(InputField f) {
		this(f,-1,1);
	}

	/**
	 * Calculate this output field.
	 * 
	 * @param subfield
	 *            Not used.
	 * @return The calculated value.
	 */
	public double calculate(final int subfield) {
		return ((this.field.getCurrentValue() - this.field.getMin()) 
				/ (this.field
				.getMax() - this.field.getMin()))
				* (this.high - this.low) + this.low;
	}

	/**
	 * @return The field that this output is based on.
	 */
	public InputField getField() {
		return this.field;
	}

	/**
	 * @return The high value of the range to map into.
	 */
	public double getHigh() {
		return this.high;
	}

	/**
	 * @return The low value of the range to map into.
	 */
	public double getLow() {
		return this.low;
	}

	/**
	 * @return This field only produces one value, so this will return 1.
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
	 * Convert a number back after its been normalized.
	 * @param data The number to convert back.
	 * @return The result.
	 */
	public double convertBack(final double data) {
		double result = ((field.getMin() - field.getMax()) * data - high
				* field.getMin() + field.getMax() * low)
				/ (low - high);
		return result;
	}

}
