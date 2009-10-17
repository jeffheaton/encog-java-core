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

package org.encog.normalize.input;

import org.encog.normalize.NormalizationError;
import org.encog.persist.annotations.EGAttribute;
import org.encog.persist.annotations.EGIgnore;
import org.encog.persist.annotations.EGReferenceable;

@EGReferenceable
public abstract class BasicInputField implements InputField {

	@EGAttribute
	private double min = Double.POSITIVE_INFINITY;
	@EGAttribute
	private double max = Double.NEGATIVE_INFINITY;
	@EGIgnore
	private double currentValue;

	public void applyMinMax(final double d) {
		this.min = Math.min(this.min, d);
		this.max = Math.max(this.max, d);

	}

	public double getCurrentValue() {
		return this.currentValue;
	}

	public double getMax() {
		return this.max;
	}

	public double getMin() {
		return this.min;
	}

	public double getValue(final int i) {
		throw new NormalizationError("Can't call getValue on "
				+ this.getClass().getSimpleName());
	}

	public void setCurrentValue(final double currentValue) {
		this.currentValue = currentValue;
	}

	public void setMax(final double max) {
		this.max = max;
	}

	public void setMin(final double min) {
		this.min = min;
	}

}
