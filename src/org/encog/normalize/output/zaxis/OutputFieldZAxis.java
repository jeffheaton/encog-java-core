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

package org.encog.normalize.output.zaxis;

import org.encog.normalize.NormalizationError;
import org.encog.normalize.input.InputField;
import org.encog.normalize.output.OutputFieldGroup;
import org.encog.normalize.output.OutputFieldGrouped;

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
