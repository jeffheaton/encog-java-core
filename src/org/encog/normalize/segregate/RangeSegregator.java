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
package org.encog.normalize.segregate;

import java.util.ArrayList;
import java.util.Collection;

import org.encog.normalize.Normalization;
import org.encog.normalize.input.InputField;
import org.encog.persist.annotations.EGBackPointer;

public class RangeSegregator implements Segregator {

	private final InputField sourceField;
	private final boolean include;
	private final Collection<SegregationRange> ranges = new ArrayList<SegregationRange>();
	
	@EGBackPointer
	private Normalization normalization;

	public RangeSegregator(final InputField sourceField, final boolean include) {
		this.sourceField = sourceField;
		this.include = include;
	}

	public void addRange(final double low, final double high,
			final boolean include) {
		final SegregationRange range = new SegregationRange(low, high, include);
		addRange(range);
	}

	public void addRange(final SegregationRange range) {
		this.ranges.add(range);
	}

	public Normalization getNormalization() {
		return this.normalization;
	}

	public InputField getSourceField() {
		return this.sourceField;
	}

	public void init(final Normalization normalization) {
		this.normalization = normalization;
	}

	public boolean shouldInclude() {
		final double value = this.sourceField.getCurrentValue();
		for (final SegregationRange range : this.ranges) {
			if (range.inRange(value)) {
				return range.isIncluded();
			}
		}
		return this.include;
	}

}
