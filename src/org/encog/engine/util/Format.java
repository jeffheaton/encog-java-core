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

package org.encog.engine.util;

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
	 * Private constructor.
	 */
	private Format() {
	}
}
