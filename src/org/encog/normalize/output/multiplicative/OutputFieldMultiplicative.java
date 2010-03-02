/*
 * Encog(tm) Core v2.4
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

package org.encog.normalize.output.multiplicative;

import org.encog.normalize.NormalizationError;
import org.encog.normalize.input.InputField;
import org.encog.normalize.output.OutputFieldGroup;
import org.encog.normalize.output.OutputFieldGrouped;

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
