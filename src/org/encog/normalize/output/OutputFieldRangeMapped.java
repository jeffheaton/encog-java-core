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

public class OutputFieldRangeMapped implements OutputField {

	private final InputField field;
	private final double low;
	private final double high;

	public OutputFieldRangeMapped(final InputField field, final double low,
			final double high) {
		this.field = field;
		this.low = low;
		this.high = high;
	}

	public double calculate(int subfield) {
		return ((this.field.getCurrentValue() - this.field.getMin()) / (this.field
				.getMax() - this.field.getMin()))
				* (this.high - this.low) + this.low;
	}

	public InputField getField() {
		return this.field;
	}

	public double getHigh() {
		return this.high;
	}

	public double getLow() {
		return this.low;
	}
	
	public int getSubfieldCount()
	{
		return 1;
	}
	
	/**
	 * Not needed for this sort of output field.
	 */
	public void beginRow()
	{		
	}

}
