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

import org.encog.persist.annotations.EGAttribute;

public class MappedRange {

	@EGAttribute
	private final double low;
	@EGAttribute
	private final double high;
	@EGAttribute
	private final double value;

	public MappedRange(final double low, final double high, final double value) {
		super();
		this.low = low;
		this.high = high;
		this.value = value;
	}

	public double getHigh() {
		return this.high;
	}

	public double getLow() {
		return this.low;
	}

	public double getValue() {
		return this.value;
	}

	public boolean inRange(final double d) {
		if ((d >= this.low) && (d <= this.high)) {
			return true;
		}
		return false;
	}

}
