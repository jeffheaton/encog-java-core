/*
  * Encog Neural Network and Bot Library for Java v0.5
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

	private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public static String displayDate(final Date date) {
		return sdf.format(date);
	}

	public static Date parseDate(final String when) {
		try {
			return sdf.parse(when);
		} catch (final ParseException e) {
			return null;
		}
	}

	private final BufferedReader reader;

	private final Map<String, Integer> columns = new HashMap<String, Integer>();

	private String data[];
	
	private final String delim;

	public ReadCSV(final String filename, boolean headers,char delim) throws IOException {
		this.reader = new BufferedReader(new FileReader(filename));


		this.delim = ""+delim;

		// read the column heads
		if( headers ) {
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

	public void close() throws IOException {
		this.reader.close();
	}

	public String get(final int i) {
		return this.data[i];
	}

	public String get(final String column) {
		final Integer i = this.columns.get(column.toLowerCase());
		if (i == null) {
			return null;
		}
		return this.data[i.intValue()];
	}

	public Date getDate(final String column) throws ParseException {
		final String str = get(column);
		return sdf.parse(str);
	}

	public double getDouble(final String column) {
		final String str = get(column);
		return Double.parseDouble(str);
	}
	
	public double getDouble(final int index) {
		final String str = get(index);
		return Double.parseDouble(str);
	}

	public int getInt(final String col) {
		final String str = get(col);
		try {
			return Integer.parseInt(str);
		} catch (final NumberFormatException e) {
			return 0;
		}		
	}
	
	private void initData(String line)
	{
		final StringTokenizer tok = new StringTokenizer(line, this.delim);

		int i = 0;
		while (tok.hasMoreTokens()) {
			tok.nextToken();
				i++;
			}
		
	
	this.data = new String[i];
		
	}

	public boolean next() throws IOException {
		final String line = this.reader.readLine();
		if (line == null) {
			return false;
		}

		if( this.data==null )
		{
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

}
