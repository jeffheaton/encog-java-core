/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.app.csv.normalize;

import org.encog.Encog;
import org.encog.util.csv.CSVFormat;

/**
 * This object holds the normalization stats for a column. This includes the
 * actual and desired high-low range for this column.
 */
public class NormalizationStats {

	/**
	 * The format to use.
	 */
	private CSVFormat format;
	
	/**
	 * The precision to use.
	 */
	private int precision;

	/**
	 * Access the normalized column data.
	 */
	private NormalizedField[] stats;

	/**
	 * Construct wiht defaults.  Default to English format.
	 */
	public NormalizationStats() {
		this.format = CSVFormat.DECIMAL_POINT;
		this.precision = Encog.DEFAULT_PRECISION;
	}

	/**
	 * Create a new object.
	 * 
	 * @param count
	 *            The number of columns.
	 */
	public NormalizationStats(final int count) {
		this();
		this.stats = new NormalizedField[count];
	}

	/**
	 * Construct the object from a series of NormalizedField field objects.
	 * @param normFields
	 * The fields to construct from.
	 */
	public NormalizationStats(final NormalizedField[] normFields) {
		this();
		this.stats = normFields;
	}

	/**
	 * @return the format
	 */
	public final CSVFormat getFormat() {
		return this.format;
	}

	/**
	 * @return the precision
	 */
	public final int getPrecision() {
		return this.precision;
	}

	/**
	 * @return The field stats.
	 */
	public final NormalizedField[] getStats() {
		return this.stats;
	}

	/**
	 * Scan all columns and fix any columns where the min/max are the same
	 * value. You cannot normalize when the min/max are the same values.
	 * 
	 * @param owner
	 */
	public final void init() {
		for (final NormalizedField stat : this.stats) {
			stat.fixSingleValue();
			stat.init(this);
		}
	}

	/**
	 * @param theFormat
	 *            the format to set
	 */
	public final void setFormat(final CSVFormat theFormat) {
		this.format = theFormat;
	}

	/**
	 * @param thePrecision
	 *            the precision to set
	 */
	public final void setPrecision(final int thePrecision) {
		this.precision = thePrecision;
	}

	/**
	 * Set the field stats.
	 * 
	 * @param theStats
	 *            The field stats.
	 */
	public final void setStats(final NormalizedField[] theStats) {
		this.stats = theStats;
	}

	/**
	 * @return Get the number of columns.
	 */
	public final int size() {
		return this.stats.length;
	}
}
