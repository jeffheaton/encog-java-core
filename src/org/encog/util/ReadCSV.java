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

import org.encog.EncogError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.persist.PersistError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ReadCSV: Read and parse CSV format files.
 */
public class ReadCSV {
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The standard date format to be used.
	 */
	private static final DateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Format a date.
	 * 
	 * @param date
	 *            The date to format.
	 * @return The formatted date.
	 */
	public static String displayDate(final Date date) {
		return SDF.format(date);
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
		this.reader = new BufferedReader(new InputStreamReader(is));
		this.delim = "" + delim;
		begin(headers);
	}

	/**
	 * Construct a CSV reader from a filename.
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
		try
		{
		this.reader = new BufferedReader(new FileReader(filename));
		this.delim = "" + delim;
		begin(headers);
		}
		catch(IOException e)
		{
			if( logger.isErrorEnabled())
			{
				logger.error("Exception",e);
			}
			throw new EncogError(e);
		}
	}

	/**
	 * Reader the headers.
	 * 
	 * @param headers
	 *            Are headers present.
	 */
	private void begin(final boolean headers) {
		try
		{
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
		catch(IOException e)
		{
			if( logger.isErrorEnabled())
			{
				logger.error("Exception",e);
			}

			throw new EncogError(e);
		}
	}

	/**
	 * Close the file.
	 * 
	 */
	public void close() {
		try
		{
			this.reader.close();
		}
		catch(IOException e)
		{
			if( logger.isErrorEnabled())
			{
				logger.error("Exception",e);
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
			return SDF.parse(str);
		} catch (ParseException e) {
			if( logger.isErrorEnabled())
			{
				logger.error("Exception",e);
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
		return Double.parseDouble(str);
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
		return Double.parseDouble(str);
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
			return Integer.parseInt(str);
		} catch (final NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Count the columns and create a an array to hold them.
	 * 
	 * @param line
	 *            One line from the file
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
	 * 
	 * @return True if there are more lines to read.
	 */
	public boolean next() {

		try {
			String line = this.reader.readLine();
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
		} catch (IOException e) {
			if( logger.isErrorEnabled())
			{
				logger.error("Exception",e);
			}

			throw new EncogError(e);
		}
		
	}
	
	public static void toCommas(StringBuilder result,double[] data)
	{
		result.setLength(0);
		for(int i=0;i<data.length;i++)
		{
			if( i!=0 )
				result.append(',');
			result.append(data[i]);
		}
	}
	
	public static double[] fromCommas(String str)
	{
		// first count the numbers
		int count = 0;
		StringTokenizer tok = new StringTokenizer(str,",");
		while(tok.hasMoreTokens())
		{
			tok.nextToken();
			count++;
		}
		
		// now allocate an object to hold that many numbers
		double[] result = new double[count];
		
		// and finally parse the numbers
		int index = 0;
		StringTokenizer tok2 = new StringTokenizer(str,",");
		while(tok2.hasMoreTokens())
		{
			try
			{
				String num = tok2.nextToken();
				double value = Double.parseDouble(num);
				result[index++] = value;
			}
			catch(NumberFormatException e)
			{
				throw new PersistError(e);
			}
			
		}
		
		return result;
	}

}
