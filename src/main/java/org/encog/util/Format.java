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
package org.encog.util;

import java.text.NumberFormat;

/**
 * Provides the ability for Encog to format numbers and times.
 */
public final class Format {

	/**
	 * Seconds in a minute.
	 */
	public static final int SECONDS_INA_MINUTE = 60;

	/**
	 * Seconds in an hour.
	 */
	public static final int SECONDS_INA_HOUR = Format.SECONDS_INA_MINUTE * 60;

	/**
	 * Seconds in a day.
	 */
	public static final int SECONDS_INA_DAY = Format.SECONDS_INA_HOUR * 24;

	/**
	 * Bytes in a KB.
	 */
	public static final long MEMORY_K = 1024;

	/**
	 * Bytes in a MB.
	 */
	public static final long MEMORY_MEG = (1024 * Format.MEMORY_K);

	/**
	 * Bytes in a GB.
	 */
	public static final long MEMORY_GIG = (1024 * Format.MEMORY_MEG);

	/**
	 * Bytes in a TB.
	 */
	public static final long MEMORY_TERA = (1024 * Format.MEMORY_GIG);

	/**
	 * How many miliseconds in a second.
	 */
	public static final long MILI_IN_SEC = 1000;

	/**
	 * One hundred percent.
	 */
	public static final double HUNDRED_PERCENT = 100.0;

	/**
	 * Format a double.
	 * 
	 * @param d
	 *            The double value to format.
	 * @param i
	 *            The number of decimal places.
	 * @return The double as a string.
	 */
	public static String formatDouble(final double d, final int i) {
		final NumberFormat f = NumberFormat.getNumberInstance();
		f.setMinimumFractionDigits(i);
		return f.format(d);
	}

	/**
	 * Format an integer.
	 * 
	 * @param i
	 *            The integer to format.
	 * @return The integer as a string.
	 */
	public static String formatInteger(final int i) {
		final NumberFormat f = NumberFormat.getIntegerInstance();
		return f.format(i);
	}

	/**
	 * Format a memory amount, to something like 32 MB.
	 * 
	 * @param memory
	 *            The amount of bytes.
	 * @return The formatted memory size.
	 */
	public static String formatMemory(final long memory) {
		if (memory < Format.MEMORY_K) {
			return memory + " bytes";
		} else if (memory < Format.MEMORY_MEG) {
			return Format.formatDouble(((double) memory)
					/ ((double) Format.MEMORY_K), 2)
					+ " KB";
		} else if (memory < Format.MEMORY_GIG) {
			return Format.formatDouble(((double) memory)
					/ ((double) Format.MEMORY_MEG), 2)
					+ " MB";
		} else if (memory < Format.MEMORY_TERA) {
			return Format.formatDouble(((double) memory)
					/ ((double) Format.MEMORY_GIG), 2)
					+ " GB";
		} else {
			return Format.formatDouble(((double) memory)
					/ ((double) Format.MEMORY_TERA), 2)
					+ " TB";
		}
	}

	/**
	 * Format a percent. Using 6 decimal places.
	 * 
	 * @param e
	 *            The percent to format.
	 * @return The formatted percent.
	 */
	public static String formatPercent(final double e) {
		if( Double.isNaN(e) || Double.isInfinite(e) ) 
			return "NaN";
		final NumberFormat f = NumberFormat.getPercentInstance();
		f.setMinimumFractionDigits(6);
		return f.format(e);
	}

	/**
	 * Format a percent with no decimal places.
	 * 
	 * @param e
	 *            The format to percent.
	 * @return The formatted percent.
	 */
	public static String formatPercentWhole(final double e) {
		final NumberFormat f = NumberFormat.getPercentInstance();
		return f.format(e);
	}

	/**
	 * Format a time span as seconds, minutes, hours and days.
	 * 
	 * @param seconds
	 *            The number of seconds in the timespan.
	 * @return The formatted timespan.
	 */
	public static String formatTimeSpan(final int seconds) {
		int secondsCount = seconds;
		final int days = seconds / Format.SECONDS_INA_DAY;
		secondsCount -= days * Format.SECONDS_INA_DAY;
		final int hours = secondsCount / Format.SECONDS_INA_HOUR;
		secondsCount -= hours * Format.SECONDS_INA_HOUR;
		final int minutes = secondsCount / Format.SECONDS_INA_MINUTE;
		secondsCount -= minutes * Format.SECONDS_INA_MINUTE;

		final NumberFormat f = NumberFormat.getIntegerInstance();
		f.setMinimumIntegerDigits(2);
		f.setMaximumIntegerDigits(2);
		final StringBuilder result = new StringBuilder();

		if (days > 0) {
			result.append(days);
			if (days > 1) {
				result.append(" days ");
			} else {
				result.append(" day ");
			}
		}

		result.append(f.format(hours));
		result.append(':');
		result.append(f.format(minutes));
		result.append(':');
		result.append(f.format(secondsCount));

		return result.toString();
	}
	
	/**
	 * Format a boolean as yes/no.
	 * @param b The boolean value.
	 * @return The yes/no result.
	 */
	public static String formatYesNo(final boolean b) {
		if (b) {
			return "Yes";
		} else {
			return "No";
		}
	}

	/**
	 * Private constructor.
	 */
	private Format() {
	}
}
