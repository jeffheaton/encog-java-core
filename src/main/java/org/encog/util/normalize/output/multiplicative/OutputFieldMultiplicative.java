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

import org.encog.util.normalize.NormalizationError;
import org.encog.util.normalize.input.InputField;
import org.encog.util.normalize.output.OutputFieldGroup;
import org.encog.util.normalize.output.OutputFieldGrouped;

/**
 * Both the multiplicative and z-axis normalization types allow a group of 
 * outputs to be adjusted so that the "vector length" is 1.  Both go about it
 * in different ways.  Certain types of neural networks require a vector length 
 * of 1.
 * 
 * The multiplicative normalization is more simple than Z-Axis normalization.  
 * Almost always Z=Axis normalization is a better choice.  However, 
 * multiplicative can perform better than Z-Axis when all of the values
 * are near zero most of the time.  This can cause the "synthetic value"
 * that z-axis uses to dominate and skew the answer.
 * 
 *  Multiplicative normalization works by calculating the vector length of
 *  the input fields and dividing each by that value.  This also presents 
 *  a problem, as the magnitude of the original fields is not used.  For 
 *  example, multiplicative normalization would not distinguish between
 *  (-2,1,3) and (-10,5,15).  Both would result in the same output.  
 *  
 * @author jheaton
 */
public class OutputFieldMultiplicative extends OutputFieldGrouped {

	/**
	 * The default constructor.  Used for reflection.
	 */
	public OutputFieldMultiplicative() {

	}

	/**
	 * Construct a multiplicative output field.
	 * @param group The group this field belongs to.
	 * @param field The input field that this field is based on.
	 */
	public OutputFieldMultiplicative(final OutputFieldGroup group,
			final InputField field) {
		super(group, field);
		if (!(group instanceof MultiplicativeGroup)) {
			throw new NormalizationError(
				"Must use MultiplicativeGroup with OutputFieldMultiplicative.");
		}
	}

	/**
	 * Calculate the value for this output field.
	 * @param subfield The subfield is not used.
	 * @return The value for this field.
	 */
	public double calculate(final int subfield) {
		return getSourceField().getCurrentValue()
				/ ((MultiplicativeGroup) getGroup()).getLength();
	}

	/**
	 * @return Always returns 1, subfields are not used for this field.
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
