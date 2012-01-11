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
import org.encog.util.normalize.input.InputField;
import org.encog.util.normalize.output.OutputFieldGroup;
import org.encog.util.normalize.output.OutputFieldGrouped;

/**
 * Both the multiplicative and z-axis normalization types allow a group of
 * outputs to be adjusted so that the "vector length" is 1. Both go about it in
 * different ways. Certain types of neural networks require a vector length of
 * 1.
 * 
 * Z-Axis normalization is usually a better choice than multiplicative. However,
 * multiplicative can perform better than Z-Axis when all of the values are near
 * zero most of the time. This can cause the "synthetic value" that z-axis uses
 * to dominate and skew the answer.
 * 
 * Z-Axis gets its name from 3D computer graphics, where there is a Z-Axis
 * extending from the plane created by the X and Y axes. It has nothing to do
 * with z-scores or the z-transform of signal theory.
 * 
 * To implement Z-Axis normalization a scaling factor must be created to
 * multiply each of the inputs against. Additionally, a synthetic field must be
 * added. It is very important that this synthetic field be added to any z-axis
 * group that you might use. The synthetic field is represented by the
 * OutputFieldZAxisSynthetic class.
 * 
 * @author jheaton
 */
public class OutputFieldZAxis extends OutputFieldGrouped {

	/**
	 * Construct a ZAxis output field.
	 * @param group The group this field belongs to.
	 * @param field The input field this is based on.
	 */
	public OutputFieldZAxis(final OutputFieldGroup group, 
			final InputField field) {
		super(group, field);
		if (!(group instanceof ZAxisGroup)) {
			throw new NormalizationError(
					"Must use ZAxisGroup with OutputFieldZAxis.");
		}
	}

	/**
	 * Calculate the current value for this field.
	 * 
	 * @param subfield
	 *            Ignored, this field type does not have subfields.
	 * @return The current value for this field.
	 */
	public double calculate(final int subfield) {
		return (getSourceField().getCurrentValue() * ((ZAxisGroup) getGroup())
				.getMultiplier());
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
