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

package org.encog.normalize.output;

import org.encog.normalize.input.InputField;
import org.encog.persist.annotations.EGReference;

/**
 * A direct output field, will simply pass the input value to the output.
 */
public class OutputFieldDirect extends BasicOutputField {

	/**
	 * The source field.
	 */
	@EGReference
	private InputField sourceField;

	/**
	 * Construct a direct output field.
	 * @param sourceField The source field to pass directly on.
	 */
	public OutputFieldDirect(final InputField sourceField) {
		this.sourceField = sourceField;
	}

	/**
	 * Default constructor, used for reflection.
	 */
	public OutputFieldDirect() {

	}

	/**
	 * Calculate the value for this field. This will simply be the
	 * value from the input field.
	 * @param subfield Not used, as this output field type does not
	 * support subfields.
	 * @return The calculated value for this field.
	 */
	public double calculate(final int subfield) {
		return this.sourceField.getCurrentValue();
	}

	/**
	 * @return Always returns 1, as subfields are not used.
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
	 * {@inheritDoc}
	 */
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("Direct: ");
		result.append("Source->");
		result.append(this.sourceField.toString());
		return result.toString();
	}

}
