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

import org.encog.util.normalize.NormalizationError;
import org.encog.util.normalize.output.OutputFieldGroup;
import org.encog.util.normalize.output.OutputFieldGrouped;

/**
 * This field represents the synthetic value used in Z-Axis normalization.
 * For more information see the OutputFieldZAxis class.
 */
public class OutputFieldZAxisSynthetic extends OutputFieldGrouped {

	/**
	 * Construct a synthetic output field for Z-Axis. 
	 * @param group The Z-Axis group that this belongs to.
	 */
	public OutputFieldZAxisSynthetic(final OutputFieldGroup group) {
		super(group, null);
		if (!(group instanceof ZAxisGroup)) {
			throw new NormalizationError(
					"Must use ZAxisGroup with OutputFieldZAxisSynthetic.");
		}
	}

	/**
	 * Calculate the synthetic value for this Z-Axis normalization.
	 * @param subfield Not used.
	 * @return The calculated value.
	 */
	public double calculate(final int subfield) {
		final double l = ((ZAxisGroup) getGroup()).getLength();
		final double f = ((ZAxisGroup) getGroup()).getMultiplier();
		final double n = getGroup().getGroupedFields().size();
		final double result = f * Math.sqrt(n - (l * l));
		if (Double.isInfinite(result) || Double.isNaN(result)) {
			return 0;
		} else {
			return result;
		}
	}

	/**
	 * @return The subfield count, which is one, as this field type does not
	 *         have subfields.
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
