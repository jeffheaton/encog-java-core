/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

package org.encog.util.csv;

import java.text.NumberFormat;
import java.util.Locale;

import org.encog.persist.annotations.EGAttribute;
import org.encog.persist.annotations.EGIgnore;

/**
 * Specifies a CSV format. This allows you to determine if a decimal point or
 * decimal comma is uses. It also specifies the character that should be used to
 * separate numbers.
 * 
 */
public class CSVFormat {

	/**
	 * Use a decimal point, and a comma to separate numbers.
	 */
	public static final CSVFormat DECIMAL_POINT = new CSVFormat('.', ',');

	/**
	 * Use a decimal comma, and a semicolon to separate numbers.
	 */
	public static final CSVFormat DECIMAL_COMMA = new CSVFormat(',', ';');

	/**
	 * Decimal point is typically used in English speaking counties.
	 */
	public static final CSVFormat ENGLISH = CSVFormat.DECIMAL_POINT;

	/**
	 * EG files, internally use a decimal point and comma separator.
	 */
	public static final CSVFormat EG_FORMAT = CSVFormat.DECIMAL_POINT;

	/**
	 * Get the decimal character currently in use by the computer's default
	 * location.
	 * 
	 * @return The decimal character used.
	 */
	public static char getDecimalCharacter() {
		final NumberFormat nf = NumberFormat.getInstance();
		final String str = nf.format(0.5);

		// there is PROBABLY a better way to do this, but I could not find it.
		// Basically we want to know the decimal separator for the current
		// locale. So we get the default number formatter and loop until we
		// find the fractional char for 0.5. Which may be "0,5" in some areas.
		for (int i = 0; i < str.length(); i++) {
			final char ch = str.charAt(i);
			if (!Character.isDigit(ch)) {
				return ch;
			}
		}

		// for some reason, we failed to find it. This should never happen.
		// But if it does, just return a decimal point.
		return '.';
	}

	/**
	 * The decimal character.
	 */
	@EGAttribute
	private final char decimal;

	/**
	 * The separator character.
	 */
	@EGAttribute
	private final char separator;

	/**
	 * The number formatter to use for this format.
	 */
	@EGIgnore
	private final NumberFormat numberFormatter;

	/**
	 * By default use USA conventions.
	 */
	public CSVFormat() {
		this('.', ',');
	}

	/**
	 * Construct a CSV format with he specified decimal and separator
	 * characters.
	 * 
	 * @param decimal
	 *            The decimal character.
	 * @param separator
	 *            The separator character.
	 */
	public CSVFormat(final char decimal, final char separator) {
		super();
		this.decimal = decimal;
		this.separator = separator;

		if (decimal == '.') {
			this.numberFormatter = NumberFormat.getInstance(Locale.US);
		} else if (decimal == ',') {
			this.numberFormatter = NumberFormat.getInstance(Locale.FRANCE);
		} else {
			this.numberFormatter = NumberFormat.getInstance();
		}
	}

	/**
	 * Format the specified number to a string with the specified number of
	 * fractional digits.
	 * 
	 * @param d
	 *            The number to format.
	 * @param digits
	 *            The number of fractional digits.
	 * @return The number formatted as a string.
	 */
	public String format(final double d, final int digits) {
		this.numberFormatter.setGroupingUsed(false);
		this.numberFormatter.setMaximumFractionDigits(digits);
		return this.numberFormatter.format(d);
	}

	/**
	 * @return The decimal character.
	 */
	public char getDecimal() {
		return this.decimal;
	}

	/**
	 * @return The number formatter.
	 */
	public NumberFormat getNumberFormatter() {
		return this.numberFormatter;
	}

	/**
	 * @return The separator character.
	 */
	public char getSeparator() {
		return this.separator;
	}

	/**
	 * Parse the specified string to a double.
	 * 
	 * @param str
	 *            The string to parse.
	 * @return The parsed number.
	 */
	public double parse(final String str) {
		try {
			return this.numberFormatter.parse(str).doubleValue();
		} catch (final Exception e) {
			throw new CSVError("Error:" + e.getMessage() + " on [" + str + "], decimal:" + this.decimal + ",sep: " + this.separator);
		}
	}
}
