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
package org.encog.util.normalize.output.multiplicative;

import org.encog.util.normalize.output.BasicOutputFieldGroup;
import org.encog.util.normalize.output.OutputFieldGrouped;

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
