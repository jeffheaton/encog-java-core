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
import org.encog.normalize.output.OutputField;

public class OutputFieldEncode implements OutputField {

	private final InputField sourceField;
	private double catchAll;
	private final List<MappedRange> ranges = new ArrayList<MappedRange>();

	public OutputFieldEncode(final InputField sourceField) {
		this.sourceField = sourceField;
	}

	public void addRange(final double low, final double high, final double value) {
		final MappedRange range = new MappedRange(low, high, value);
		this.ranges.add(range);
	}

	public double calculate(int subfield) {
		for (final MappedRange range : this.ranges) {
			if (range.inRange(this.sourceField.getCurrentValue())) {
				return range.getValue();
			}
		}

		return this.catchAll;
	}
	
	public int getSubfieldCount()
	{
		return 1;
	}

	public double getCatchAll() {
		return this.catchAll;
	}

	public InputField getSourceField() {
		return this.sourceField;
	}

	public void setCatchAll(final double catchAll) {
		this.catchAll = catchAll;
	}

	/**
	 * Not needed for this sort of output field.
	 */
	public void beginRow()
	{		
	}

	
}
