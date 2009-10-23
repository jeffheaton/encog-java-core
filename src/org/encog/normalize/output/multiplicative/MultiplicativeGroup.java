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
package org.encog.normalize.output.multiplicative;

import org.encog.normalize.output.BasicOutputFieldGroup;
import org.encog.normalize.output.OutputFieldGrouped;

/**
 * Used to group multiplicative fields together.
 */
public class MultiplicativeGroup extends BasicOutputFieldGroup {

	/**
	 * The "length" of this field.
	 */
	private double length;

	/**
	 * @return The length of this field.  This is the sum of the squares of
	 * all of the groupped fields.  The square root of this sum is the 
	 * length. 
	 */
	public double getLength() {
		return this.length;
	}

	/**
	 * Called to init this group for a new field.  This recalculates the
	 * "length".
	 */
	public void rowInit() {
		double value = 0;

		for (final OutputFieldGrouped field : getGroupedFields()) {
			value += (field.getSourceField().getCurrentValue() * field
					.getSourceField().getCurrentValue());
		}
		this.length = Math.sqrt(value);
	}

}
