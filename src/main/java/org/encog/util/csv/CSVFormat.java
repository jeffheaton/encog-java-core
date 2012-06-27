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
package org.encog.util.csv;

import java.io.Serializable;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Specifies a CSV format. This allows you to determine if a decimal point or
 * decimal comma is uses. It also specifies the character that should be used to
 * separate numbers.
 * 
 */
public class CSVFormat implements Serializable {

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
		return DecimalFormatSymbols.getInstance().getDecimalSeparator(); 
	}

	/**
	 * The decimal character.
	 */
	private final char decimal;

	/**
	 * The separator character.
	 */
	private final char separator;

	/**
	 * The number formatter to use for this format.
	 */
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
	public synchronized String format(final double d, final int digits) {
		if( Double.isInfinite(d) || Double.isNaN(d) ) 
			return "0";
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
	public synchronized double parse(final String str) {
		try {
			if( str.equals("?")) { 
				return Double.NaN;
			} if( str.equalsIgnoreCase("NaN") ) {
				return Double.NaN;
			} else {
				return this.numberFormatter.parse(str.trim()).doubleValue();
			}
		} catch (final Exception e) {
			throw new CSVError("Error:" + e.getMessage() + " on [" + str + "], decimal:" + this.decimal + ",sep: " + this.separator);
		}
	}
}
