/**
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

package org.encog.normalize.output;

import org.encog.normalize.input.InputField;
import org.encog.persist.annotations.EGAttribute;
import org.encog.persist.annotations.EGReference;

/**
 * A ranged mapped output field.  This will scale the input so that it
 * is between the high and low value.
 */
public class OutputFieldRangeMapped extends BasicOutputField implements RequireTwoPass {

	/**
	 * The input field to scale.
	 */
	@EGReference
	private InputField field;

	/**
	 * The low value of the field.
	 */
	@EGAttribute
	private double low;
	
	/**
	 * The high value of the field.
	 */
	@EGAttribute
	private double high;

	/**
	 * Default constructor, used mainly for reflection.
	 */
	public OutputFieldRangeMapped() {

	}

	/**
	 * Construct a range mapped output field.
	 * @param field The input field to base this on.
	 * @param low The low value.
	 * @param high The high value.
	 */
	public OutputFieldRangeMapped(final InputField field, final double low,
			final double high) {
		this.field = field;
		this.low = low;
		this.high = high;
	}

	/**
	 * Calculate this output field.
	 * @param subfield Not used.
	 * @return The calculated value.
	 */
	public double calculate(final int subfield) {
		return ((this.field.getCurrentValue() - this.field.getMin()) / (this.field
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

}
