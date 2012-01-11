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
package org.encog.app.analyst.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.app.analyst.AnalystError;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Utility class to help deal with CSV headers.
 */
public class CSVHeaders {

	/**
	 * Parse a timeslice from a header such as (t-1).
	 * @param name The column name.
	 * @return The timeslice.
	 */
	public static int parseTimeSlice(final String name) {
		final int index1 = name.indexOf('(');
		if (index1 == -1) {
			return 0;
		}
		final int index2 = name.indexOf(')');
		if (index2 == -1) {
			return 0;
		}
		if (index2 < index1) {
			return 0;
		}
		final String list = name.substring(index1 + 1, index2);
		final String[] values = list.split(",");
		for (final String value : values) {
			final String str = value.trim();
			if (str.toLowerCase().startsWith("t")) {
				final int slice = Integer.parseInt(str.substring(1));
				return slice;
			}
		}

		return 0;
	}

	/**
	 * Tag a column with part # and timeslice.
	 * @param name The name of the column.
	 * @param part The part #.
	 * @param timeSlice The timeslice.
	 * @param multiPart True if this is a multipart column.
	 * @return The new tagged column.
	 */
	public static String tagColumn(final String name, final int part,
			final int timeSlice, final boolean multiPart) {
		final StringBuilder result = new StringBuilder();
		result.append(name);

		// is there any suffix?
		if (multiPart || (timeSlice != 0)) {
			result.append('(');

			// is there a part?
			if (multiPart) {
				result.append('p');
				result.append(part);
			}

			// is there a timeslice?
			if (timeSlice != 0) {
				if (multiPart) {
					result.append(',');
				}
				result.append('t');
				if (timeSlice > 0) {
					result.append('+');
				}
				result.append(timeSlice);

			}

			result.append(')');
		}
		return result.toString();
	}

	/**
	 * The header list.
	 */
	private final List<String> headerList = new ArrayList<String>();

	/**
	 * The column mapping, maps column name to column index.
	 */
	private final Map<String, Integer> columnMapping 
		= new HashMap<String, Integer>();

	/**
	 * Construct the object.
	 * @param filename The filename.
 	 * @param headers False if headers are not extended.
	 * @param format The CSV format.
	 */
	public CSVHeaders(final File filename, final boolean headers,
			final CSVFormat format) {
		ReadCSV csv = null;
		try {
			csv = new ReadCSV(filename.toString(), headers, format);
			if (csv.next()) {
				if (headers) {
					for (final String str : csv.getColumnNames()) {
						this.headerList.add(str);
					}
				} else {
					for (int i = 0; i < csv.getColumnCount(); i++) {
						this.headerList.add("field:" + (i + 1));
					}
				}
			}

			init();

		} finally {
			if (csv != null) {
				csv.close();
			}
		}
	}

	/**
	 * Construct the object.
	 * @param inputHeadings The input headings.
	 */
	public CSVHeaders(final List<String> inputHeadings) {
		for (final String header : inputHeadings) {
			this.headerList.add(header);
		}
		init();
	}

	/**
	 * Construct the object.
	 * @param inputHeadings The input headings.
	 */
	public CSVHeaders(final String[] inputHeadings) {
		for (final String header : inputHeadings) {
			this.headerList.add(header);
		}

		init();
	}

	/**
	 * Find the specified column.
	 * @param name The column name.
	 * @return The index of the column.
	 */
	public final int find(final String name) {
		String key = name.toLowerCase();
		
		if (!this.columnMapping.containsKey(key)) {
			throw new AnalystError("Can't find column: " + name.toLowerCase());
		}

		return this.columnMapping.get(key);
	}

	/**
	 * Get the base header, strip any (...).
	 * @param index The index of the header.
	 * @return The base header.
	 */
	public final String getBaseHeader(final int index) {
		String result = this.headerList.get(index);

		final int loc = result.indexOf('(');
		if (loc != -1) {
			result = result.substring(0, loc);
		}

		return result.trim();
	}

	/**
	 * Get the specified header.
	 * @param index The index of the header to get.
	 * @return The header value.
	 */
	public final String getHeader(final int index) {
		return this.headerList.get(index);
	}

	/**
	 * @return The headers.
	 */
	public final List<String> getHeaders() {
		return this.headerList;
	}

	/**
	 * Get the timeslice for the specified index.
	 * @param currentIndex The index to get the time slice for.
	 * @return The timeslice.
	 */
	public final int getSlice(final int currentIndex) {
		final String name = this.headerList.get(currentIndex);
		final int index1 = name.indexOf('(');
		if (index1 == -1) {
			return 0;
		}
		final int index2 = name.indexOf(')');
		if (index2 == -1) {
			return 0;
		}
		if (index2 < index1) {
			return 0;
		}
		final String list = name.substring(index1 + 1, index2);
		final String[] values = list.split(",");
		for (final String value : values) {
			String str = value.trim();
			if (str.toLowerCase().startsWith("t")) {
				str = value.trim().substring(1).trim();
				if (str.charAt(0) == '+') {
					// since Integer.parseInt can't handle +1
					str = str.substring(1);
				}
				final int slice = Integer.parseInt(str);
				return slice;
			}
		}

		return 0;
	}

	/**
	 * Setup the column mapping and validate.
	 */
	private void init() {
		int index = 0;
		for (final String str : this.headerList) {
			this.columnMapping.put(str.toLowerCase(), index++);
		}

		validateSameName();
	}

	/**
	 * @return The number of headers.
	 */
	public final int size() {
		return this.headerList.size();
	}

	/**
	 * Validate that two columns do not have the same name.  This is an error.
	 */
	private void validateSameName() {
		for (int i = 0; i < this.headerList.size(); i++) {
			for (int j = 0; j < this.headerList.size(); j++) {
				if (i == j) {
					continue;
				}

				if (this.headerList.get(i).equalsIgnoreCase(
						this.headerList.get(j))) {
					throw new AnalystError("Multiple fields named: "
							+ this.headerList.get(i));
				}
			}
		}
	}

}
