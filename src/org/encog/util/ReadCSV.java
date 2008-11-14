/*
 * Encog Neural Network and Bot Library for Java v1.x
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008, Heaton Research Inc., and individual contributors.
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
package org.encog.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * ReadCSV: Read and parse CSV format files.
 */
public class ReadCSV {

	/**
	 * The standard date format to be used.
	 */
	private static final DateFormat SDF = 
		new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Format a date.
	 * @param date The date to format.
	 * @return The formatted date.
	 */
	public static String displayDate(final Date date) {
		return SDF.format(date);
	}

	/**
	 * Parse a date.
	 * @param when The date string.
	 * @return The parsed date.
	 */
	public static Date parseDate(final String when) {
		try {
			return SDF.parse(when);
		} catch (final ParseException e) {
			return null;
		}
	}

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
	 * The delimiter.
	 */
	private final String delim;

	/**
	 * Construct a CSV reader from an input stream.
	 * @param is The InputStream to read from.
	 * @param headers Are headers present?
	 * @param delim What is the delimiter.
	 * @throws IOException An IO error occurred.
	 */
	public ReadCSV(final InputStream is, final boolean headers, 
			final char delim)
			throws IOException {
		this.reader = new BufferedReader(new InputStreamReader(is));
		this.delim = "" + delim;
		begin(headers);
	}

	/**
	 * Construct a CSV reader from a filename.
	 * @param filename The filename.
	 * @param headers The headers.
	 * @param delim The delimiter.
	 * @throws IOException An IO exception occured.
	 */
	public ReadCSV(final String filename, final boolean headers,
			final char delim) throws IOException {
		this.reader = new BufferedReader(new FileReader(filename));
		this.delim = "" + delim;
		begin(headers);
	}

	/**
	 * Reader the headers.
	 * @param headers Are headers present.
	 * @throws IOException An IO exception happened.
	 */
	private void begin(final boolean headers) throws IOException {
		// read the column heads
		if (headers) {
			final String line = this.reader.readLine();
			final StringTokenizer tok = new StringTokenizer(line, this.delim);
			int i = 0;
			while (tok.hasMoreTokens()) {
				final String header = tok.nextToken();
				this.columns.put(header.toLowerCase(), i++);
			}
		}

		this.data = null;
	}

	/**
	 * Close the file.
	 * @throws IOException An exception occured.
	 */
	public void close() throws IOException {
		this.reader.close();
	}

	/**
	 * Get the specified column as a string.
	 * @param i The column index, starting at zero.
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
	 * Get the column as a date.
	 * @param column The column header name.  
	 * @return The column as a date.
	 * @throws ParseException If an error occured while parsing.
	 */
	public Date getDate(final String column) throws ParseException {
		final String str = get(column);
		return SDF.parse(str);
	}

	/**
	 * Get the column as a double specified by index.
	 * @param index The column index, starting at zero.
	 * @return The data at the specified column.
	 */
	public double getDouble(final int index) {
		final String str = get(index);
		return Double.parseDouble(str);
	}

	/**
	 * Get the specified column as a double.
	 * @param column The column name that we want to get.
	 * @return The column data as a double.
	 */
	public double getDouble(final String column) {
		final String str = get(column);
		return Double.parseDouble(str);
	}

	/**
	 * Obtain a column as an integer referenced by a string.
	 * @param col The column header name being read.
	 * @return The column data as an integer.
	 */
	public int getInt(final String col) {
		final String str = get(col);
		try {
			return Integer.parseInt(str);
		} catch (final NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Count the columns and create a an array to hold them.
	 * @param line One line from the file
	 */
	private void initData(final String line) {
		final StringTokenizer tok = new StringTokenizer(line, this.delim);

		int i = 0;
		while (tok.hasMoreTokens()) {
			tok.nextToken();
			i++;
		}

		this.data = new String[i];

	}

	/**
	 * Read the next line. 
	 * @return True if there are more lines to read.
	 * @throws IOException An error occured.
	 */
	public boolean next() throws IOException {
		final String line = this.reader.readLine();
		if (line == null) {
			return false;
		}

		if (this.data == null) {
			initData(line);
		}

		final StringTokenizer tok = new StringTokenizer(line, this.delim);

		int i = 0;
		while (tok.hasMoreTokens()) {
			final String str = tok.nextToken();
			if (i < this.data.length) {
				this.data[i++] = str;
			}
		}
		return true;
	}
	
	public int getColumnCount()
	{
		if(this.data==null)
			return 0;
		else
			return this.data.length;
	}
	

}
