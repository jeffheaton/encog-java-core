/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
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

package org.encog.util.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read and parse CSV format files.
 */
public class ReadCSV {

	/**
	 * Format a date.
	 * 
	 * @param date
	 *            The date to format.
	 * @return The formatted date.
	 */
	public static String displayDate(final Date date) {
		final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	/**
	 * Parse a date.
	 * 
	 * @param when
	 *            The date string.
	 * @return The parsed date.
	 */
	public static Date parseDate(final String when) {
		try {
			final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.parse(when);
		} catch (final ParseException e) {
			return null;
		}
	}

	/**
	 * The standard date format to be used.
	 */
	private final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * The CSV format to use.
	 */
	private CSVFormat format;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The buffered reader to read the file.
	 */
	private final BufferedReader reader;

	/**
	 * The names of the columns.
	 */
	private final Map<String, Integer> columns = new HashMap<String, Integer>();

	/**
	 * The data.
	 */
	private String[] data;

	/**
	 * Construct a CSV reader from an input stream. Allows a delimiter character
	 * to be specified. Numbers will be parsed using the current locale.
	 * 
	 * @param is
	 *            The InputStream to read from.
	 * @param headers
	 *            Are headers present?
	 * @param delim
	 *            What is the delimiter.
	 */
	public ReadCSV(final InputStream is, final boolean headers, 
			final char delim) {
		final CSVFormat format = new CSVFormat(CSVFormat.getDecimalCharacter(),
				delim);
		this.reader = new BufferedReader(new InputStreamReader(is));
		begin(headers, format);
	}

	/**
	 * Construct a CSV reader from an input stream. The format parameter
	 * specifies the separator character to use, as well as the number format.
	 * 
	 * @param is
	 *            The InputStream to read from.
	 * @param headers
	 *            Are headers present?
	 * @param format
	 *            What is the CSV format.
	 */
	public ReadCSV(final InputStream is, final boolean headers,
			final CSVFormat format) {
		this.reader = new BufferedReader(new InputStreamReader(is));
		begin(headers, format);
	}

	/**
	 * Construct a CSV reader from a filename. The format parameter specifies
	 * the separator character to use, as well as the number format.
	 * 
	 * @param filename
	 *            The filename.
	 * @param headers
	 *            The headers.
	 * @param delim
	 *            The delimiter.
	 */
	public ReadCSV(final String filename, final boolean headers,
			final char delim) {
		try {
			final CSVFormat format = new CSVFormat(CSVFormat
					.getDecimalCharacter(), delim);
			this.reader = new BufferedReader(new FileReader(filename));
			begin(headers, format);
		} catch (final IOException e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Exception", e);
			}
			throw new EncogError(e);
		}
	}

	/**
	 * Construct a CSV reader from a filename. Allows a delimiter character to
	 * be specified.
	 * 
	 * @param filename
	 *            The filename.
	 * @param headers
	 *            The headers.
	 * @param format
	 *            The format.
	 */
	public ReadCSV(final String filename, final boolean headers,
			final CSVFormat format) {
		try {
			this.reader = new BufferedReader(new FileReader(filename));
			begin(headers, format);
		} catch (final IOException e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Exception", e);
			}
			throw new EncogError(e);
		}
	}

	/**
	 * Reader the headers.
	 * 
	 * @param headers
	 *            Are headers present.
	 * @param format The format to use.
	 */
	private void begin(final boolean headers, final CSVFormat format) {
		try {
			this.format = format;
			// read the column heads
			if (headers) {
				final String line = this.reader.readLine();
				final List<String> tok = parse(line);

				int i = 0;
				for (final String header : tok) {
					this.columns.put(header.toLowerCase(), i++);
				}
			}

			this.data = null;
		} catch (final IOException e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Exception", e);
			}

			throw new EncogError(e);
		}
	}

	/**
	 * Close the file.
	 * 
	 */
	public void close() {
		try {
			this.reader.close();
		} catch (final IOException e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Exception", e);
			}

			throw new EncogError(e);
		}
	}

	/**
	 * Get the specified column as a string.
	 * 
	 * @param i
	 *            The column index, starting at zero.
	 * @return The column as a string.
	 */
	public String get(final int i) {
		return this.data[i];
	}

	/**
	 * Get the column by its string name, as a string. This will only work if
	 * column headers were defined that have string names.
	 * 
	 * @param column
	 *            The column name.
	 * @return The column data as a string.
	 */
	public String get(final String column) {
		final Integer i = this.columns.get(column.toLowerCase());
		if (i == null) {
			return null;
		}
		return this.data[i.intValue()];
	}

	/**
	 * Get the column count.
	 * 
	 * @return The column count.
	 */
	public int getColumnCount() {
		if (this.data == null) {
			return 0;
		}

		return this.data.length;
	}

	/**
	 * Get the column as a date.
	 * 
	 * @param column
	 *            The column header name.
	 * @return The column as a date.
	 */
	public Date getDate(final String column) {

		try {
			final String str = get(column);
			return this.sdf.parse(str);
		} catch (final ParseException e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Exception", e);
			}

			throw new EncogError(e);
		}

	}

	/**
	 * Get the column as a double specified by index.
	 * 
	 * @param index
	 *            The column index, starting at zero.
	 * @return The data at the specified column.
	 */
	public double getDouble(final int index) {
		final String str = get(index);
		return this.format.parse(str);
	}

	/**
	 * Get the specified column as a double.
	 * 
	 * @param column
	 *            The column name that we want to get.
	 * @return The column data as a double.
	 */
	public double getDouble(final String column) {
		final String str = get(column);
		return this.format.parse(str);
	}

	/**
	 * Obtain a column as an integer referenced by a string.
	 * 
	 * @param col
	 *            The column header name being read.
	 * @return The column data as an integer.
	 */
	public int getInt(final String col) {
		final String str = get(col);
		try {
			return this.format.getNumberFormatter().parse(str).intValue();
		} catch (final ParseException e) {
			throw new CSVError(e);
		}
	}

	/**
	 * Count the columns and create a an array to hold them.
	 * 
	 * @param line
	 *            One line from the file
	 */
	private void initData(final String line) {
		final List<String> tok = parse(line);
		this.data = new String[tok.size()];

	}

	/**
	 * Read the next line.
	 * 
	 * @return True if there are more lines to read.
	 */
	public boolean next() {

		try {
			final String line = this.reader.readLine();
			if (line == null) {
				return false;
			}

			if (this.data == null) {
				initData(line);
			}

			final List<String> tok = parse(line);

			int i = 0;
			for (final String str : tok) {
				if (i < this.data.length) {
					this.data[i++] = str;
				}
			}

			return true;
		} catch (final IOException e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Exception", e);
			}

			throw new EncogError(e);
		}

	}

	/**
	 * Parse the line into a list of values.
	 * 
	 * @param line
	 *            The line to parse.
	 * @return The elements on this line.
	 */
	private List<String> parse(final String line) {
		final StringBuilder item = new StringBuilder();
		final List<String> result = new ArrayList<String>();
		boolean quoted = false;

		for (int i = 0; i < line.length(); i++) {
			final char ch = line.charAt(i);
			if ((ch == this.format.getSeparator()) && !quoted) {
				result.add(item.toString());
				item.setLength(0);
				quoted = false;
			} else if ((ch == '\"') && (item.length() == 0)) {
				quoted = true;
			} else if ((ch == '\"') && quoted) {
				quoted = false;
			} else {
				item.append(ch);
			}
		}

		if (item.length() > 0) {
			result.add(item.toString());
		}

		return result;
	}

}
