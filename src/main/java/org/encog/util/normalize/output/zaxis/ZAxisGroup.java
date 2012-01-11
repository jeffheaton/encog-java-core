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
package org.encog.util.normalize.output.zaxis;

import org.encog.util.normalize.output.BasicOutputFieldGroup;
import org.encog.util.normalize.output.OutputFieldGrouped;

/**
 * Used to group Z-Axis fields together. Both OutputFieldZAxis and
 * OutputFieldZAxisSynthetic fields may belong to this group. For
 * more information see the OutputFieldZAxis class.
 * 
 */
public class ZAxisGroup extends BasicOutputFieldGroup {

	/**
	 * The calculated length.
	 */
	private double length;

	/**
	 * The multiplier, which is the value that all other values will be
	 * multiplied to become normalized.
	 */
	private double multiplier;

	/**
	 * @return The vector length.
	 */
	public double getLength() {
		return this.length;
	}

	/**
	 * @return The value to multiply the other values by to normalize them.
	 */
	public double getMultiplier() {
		return this.multiplier;
	}

	/**
	 * Initialize this group for a new row.
	 */
	public void rowInit() {
		double value = 0;

		for (final OutputFieldGrouped field : getGroupedFields()) {
			if (!(field instanceof OutputFieldZAxisSynthetic)) {
				if (field.getSourceField() != null) {
					value += (field.getSourceField().getCurrentValue() * field
							.getSourceField().getCurrentValue());
				}
			}
		}
		this.length = Math.sqrt(value);
		this.multiplier = 1.0 / Math.sqrt(getGroupedFields().size());
	}

}
