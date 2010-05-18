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

package org.encog.mathutil;

import java.util.List;

import org.encog.util.Format;

/**
 * A numeric range has a high, low, mean, root-mean-square, standard deviation,
 * and the count of how many samples it contains.
 * 
 */
public class NumericRange {
	/**
	 * The high number in the range.
	 */
	private final double high;
	
	/**
	 * The low number in the range.
	 */
	private final double low;
	
	/**
	 * The mean value.
	 */
	private final double mean;
	
	/**
	 * The root mean square of the range.
	 */
	private final double rms;
	
	/**
	 * The standard deviation of the range.
	 */
	private final double standardDeviation;
	
	/**
	 * The number of values in this range.
	 */
	private final int samples;

	/**
	 * Create a numeric range from a list of values.
	 * @param values The values to calculate for.
	 */
	public NumericRange(List<Double> values) {

		double assignedHigh = 0;
		double assignedLow = 0;
		double total = 0;
		double rmsTotal = 0;

		// get the mean and other 1-pass values.

		for (double d : values) {
			assignedHigh = Math.max(assignedHigh, d);
			assignedLow = Math.min(assignedLow, d);
			total += d;
			rmsTotal += d * d;
		}

		this.samples = values.size();
		this.high = assignedHigh;
		this.low = assignedLow;
		this.mean = total / this.samples;
		this.rms = Math.sqrt(rmsTotal / this.samples);

		// now get the standard deviation
		double devTotal = 0;

		for (double d : values) {
			devTotal += Math.pow(d - this.mean, 2);
		}
		this.standardDeviation = Math.sqrt(devTotal / this.samples);
	}

	/**
	 * @return The high number in the range.
	 */
	public double getHigh() {
		return high;
	}

	/**
	 * @return The low number in the range.
	 */
	public double getLow() {
		return low;
	}

	/**
	 * @return The mean in the range.
	 */
	public double getMean() {
		return mean;
	}

	/**
	 * @return The root mean square of the range.
	 */
	public double getRms() {
		return rms;
	}

	/**
	 * @return The standard deviation of the range.
	 */
	public double getStandardDeviation() {
		return standardDeviation;
	}

	/**
	 * @return The number of samples in the range.
	 */
	public int getSamples() {
		return samples;
	}

	/**
	 * @return The range as a string.
	 */
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Range: ");
		result.append(Format.formatDouble(this.low, 5));
		result.append(" to ");
		result.append(Format.formatDouble(this.high, 5));
		result.append(",samples: ");
		result.append(Format.formatInteger(this.samples));
		result.append(",mean: ");
		result.append(Format.formatDouble(this.mean, 5));
		result.append(",rms: ");
		result.append(Format.formatDouble(this.rms, 5));
		result.append(",s.deviation: ");
		result.append(Format.formatDouble(this.standardDeviation, 5));

		return result.toString();
	}
}
