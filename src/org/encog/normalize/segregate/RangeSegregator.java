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

package org.encog.normalize.segregate;

import java.util.ArrayList;
import java.util.Collection;

import org.encog.normalize.DataNormalization;
import org.encog.normalize.input.InputField;
import org.encog.persist.annotations.EGReference;

/**
 * Range segregators are used to segregate data and include or exclude if it is
 * within a certain range.
 */
public class RangeSegregator implements Segregator {

	/**
	 * The source field that this is based on.
	 */
	@EGReference
	private InputField sourceField;

	/**
	 * If none of the ranges match, should this data be included.
	 */
	private boolean include;

	/**
	 * The ranges.
	 */
	private final Collection<SegregationRange> ranges = 
		new ArrayList<SegregationRange>();

	/**
	 * The normalization object.
	 */
	@EGReference
	private DataNormalization normalization;

	/**
	 * Default constructor for reflection.
	 */
	public RangeSegregator() {
	}

	/**
	 * Construct a range segregator.
	 * 
	 * @param sourceField
	 *            The source field.
	 * @param include
	 *            Default action, if the data is not in any of the ranges,
	 *            should it be included.
	 */
	public RangeSegregator(final InputField sourceField, 
			final boolean include) {
		this.sourceField = sourceField;
		this.include = include;
	}

	/**
	 * Add a range.
	 * 
	 * @param low
	 *            The low end of the range.
	 * @param high
	 *            The high end of the range.
	 * @param include
	 *            Should this range be included.
	 */
	public void addRange(final double low, final double high,
			final boolean include) {
		final SegregationRange range = new SegregationRange(low, high, include);
		addRange(range);
	}

	/**
	 * Add a range.
	 * 
	 * @param range
	 *            The range to add.
	 */
	public void addRange(final SegregationRange range) {
		this.ranges.add(range);
	}

	/**
	 * @return The normalization object used by this object.
	 */
	public DataNormalization getNormalization() {
		return this.normalization;
	}

	/**
	 * @return The source field that the ranges are compared against.
	 */
	public InputField getSourceField() {
		return this.sourceField;
	}

	/**
	 * Init the object.
	 * @param normalization The normalization object that owns this range.
	 */
	public void init(final DataNormalization normalization) {
		this.normalization = normalization;
	}

	/**
	 * @return True if the current row should be included according to this
	 *         segregator.
	 */
	public boolean shouldInclude() {
		final double value = this.sourceField.getCurrentValue();
		for (final SegregationRange range : this.ranges) {
			if (range.inRange(value)) {
				return range.isIncluded();
			}
		}
		return this.include;
	}

	/**
	 * Nothing needs to be done to setup for a pass.
	 */
	public void passInit() {		
	}

}
