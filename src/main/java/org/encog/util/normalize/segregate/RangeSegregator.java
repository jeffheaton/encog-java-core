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
package org.encog.util.normalize.segregate;

import java.util.ArrayList;
import java.util.Collection;

import org.encog.util.normalize.DataNormalization;
import org.encog.util.normalize.input.InputField;

/**
 * Range segregators are used to segregate data and include or exclude if it is
 * within a certain range.
 */
public class RangeSegregator implements Segregator {

	/**
	 * The source field that this is based on.
	 */
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
