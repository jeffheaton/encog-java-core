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
package org.encog.util.csv;

import java.text.NumberFormat;
import java.util.Locale;

import org.encog.persist.annotations.EGAttribute;
import org.encog.persist.annotations.EGIgnore;

/**
 * Specifies a CSV format.  This allows you to determine if a decimal
 * point or decimal comma is uses.  It also specifies the character
 * that should be used to separate numbers.
 *
 */
public class CSVFormat {

	/**
	 * Use a decimal point, and a comma to separate numbers.
	 */
	public static CSVFormat DECIMAL_POINT = new CSVFormat('.', ',');
	
	/**
	 * Use a decimal comma, and a semicolon to separate numbers.
	 */
	public static CSVFormat DECIMAL_COMMA = new CSVFormat(',', ';');
	
	/**
	 * Decimal point is typically used in English speaking counties.
	 */
	public static CSVFormat ENGLISH = CSVFormat.DECIMAL_POINT;
	
	/**
	 * EG files, internally use a decimal point and comma separator.
	 */
	public static final CSVFormat EG_FORMAT = CSVFormat.DECIMAL_POINT;

	/**
	 * Get the decimal character currently in use by the computer's default
	 * location.
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

	public CSVFormat()
	{
		this('.',',');
	}
	
	/**
	 * Construct a CSV format with he specified decimal and separator
	 * characters.
	 * @param decimal The decimal character.
	 * @param separator The separator character.
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
	 * Format the specified number to a string with the specified number
	 * of fractional digits.
	 * @param d The number to format.
	 * @param digits The number of fractional digits.
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
	 * @param str The string to parse.
	 * @return The parsed number.
	 */
	public double parse(final String str) {
		try {
			return this.numberFormatter.parse(str).doubleValue();
		} catch (final Exception e) {
			throw new CSVError(e);
		}
	}
}
