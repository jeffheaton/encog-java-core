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
package org.encog.app.analyst.csv.basic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.app.quant.QuantError;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.logging.EncogLogging;

/**
 * Forms the foundation of all of the cached files in Encog Quant.
 */
public class BasicCachedFile extends BasicFile {

	/**
	 * The column mapping.
	 */
	private final Map<String, BaseCachedColumn> columnMapping 
		= new HashMap<String, BaseCachedColumn>();

	/**
	 * The columns.
	 */
	private final List<BaseCachedColumn> columns 
		= new ArrayList<BaseCachedColumn>();

	/**
	 * Add a new column.
	 * 
	 * @param column
	 *            The column to add.
	 */
	public final void addColumn(final BaseCachedColumn column) {
		this.columns.add(column);
		this.columnMapping.put(column.getName(), column);
	}

	/**
	 * Analyze the input file.
	 * 
	 * @param input
	 *            The input file.
	 * @param headers
	 *            True, if there are headers.
	 * @param format
	 *            The format of the CSV data.
	 */
	public final void analyze(final File input, final boolean headers,
			final CSVFormat format) {
		resetStatus();
		setInputFilename(input);
		setExpectInputHeaders(headers);
		setInputFormat(format);
		this.columnMapping.clear();
		this.columns.clear();

		// first count the rows
		BufferedReader reader = null;
		try {
			int recordCount = 0;
			reader = new BufferedReader(new FileReader(getInputFilename()));
			while (reader.readLine() != null) {
				updateStatus(true);
				recordCount++;
			}

			if (headers) {
				recordCount--;
			}
			setRecordCount(recordCount);
		} catch (final IOException ex) {
			throw new QuantError(ex);
		} finally {
			reportDone(true);
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
					throw new QuantError(e);
				}
			}
			setInputFilename(input);
			setExpectInputHeaders(headers);
			setInputFormat(format);
		}

		// now analyze columns
		ReadCSV csv = null;
		try {
			csv = new ReadCSV(input.toString(), headers, format);
			if (!csv.next()) {
				throw new QuantError("File is empty");
			}

			for (int i = 0; i < csv.getColumnCount(); i++) {
				String name;

				if (headers) {
					name = attemptResolveName(csv.getColumnNames().get(i));
				} else {
					name = "Column-" + (i + 1);
				}

				// determine if it should be an input/output field

				final String str = csv.get(i);

				boolean io = false;

				try {
					Double.parseDouble(str);
					io = true;
				} catch (final NumberFormatException ex) {
					EncogLogging.log(ex);
				}

				addColumn(new FileData(name, i, io, io));
			}
		} finally {
			csv.close();
			setAnalyzed(true);
		}
	}

	/**
	 * Attempt to resolve a column name.
	 * 
	 * @param name
	 *            The unknown column name.
	 * @return The known column name.
	 */
	private String attemptResolveName(final String name) {
		String name2 = name.toLowerCase();

		if (name2.indexOf("open") != -1) {
			return FileData.OPEN;
		} else if (name2.indexOf("close") != -1) {
			return FileData.CLOSE;
		} else if (name2.indexOf("low") != -1) {
			return FileData.LOW;
		} else if (name2.indexOf("hi") != -1) {
			return FileData.HIGH;
		} else if (name2.indexOf("vol") != -1) {
			return FileData.VOLUME;
		} else if ((name2.indexOf("date") != -1) 
				|| (name.indexOf("yyyy") != -1)) {
			return FileData.DATE;
		} else if (name2.indexOf("time") != -1) {
			return FileData.TIME;
		}

		return name;
	}

	/**
	 * Get the data for a specific column.
	 * 
	 * @param name
	 *            The column to read.
	 * @param csv
	 *            The CSV file to read from.
	 * @return The column data.
	 */
	public final String getColumnData(final String name, final ReadCSV csv) {
		if (!this.columnMapping.containsKey(name)) {
			return null;
		}

		final BaseCachedColumn column = this.columnMapping.get(name);

		if (!(column instanceof FileData)) {
			return null;
		}

		final FileData fd = (FileData) column;
		return csv.get(fd.getIndex());
	}

	/**
	 * @return The column mappings.
	 */
	public final Map<String, BaseCachedColumn> getColumnMapping() {
		return this.columnMapping;
	}

	/**
	 * @return The columns.
	 */
	public final List<BaseCachedColumn> getColumns() {
		return this.columns;
	}

}
