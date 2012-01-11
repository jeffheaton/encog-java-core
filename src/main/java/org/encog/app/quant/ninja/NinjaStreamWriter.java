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
package org.encog.app.quant.ninja;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.encog.Encog;
import org.encog.app.analyst.csv.basic.FileData;
import org.encog.app.quant.QuantError;
import org.encog.util.csv.CSVFormat;
import org.encog.util.time.NumericDateUtil;

/**
 * Can be used from within NinjaTrader to export data. This class is usually
 * placed inside of a NinjaTrader indicator to export NinjaTrader indicators and
 * data.
 * 
 * Ninja Trader, at this point, only directly supports C#. So this class will be
 * of limited use on the Java platform.
 */
public class NinjaStreamWriter {

	/**
	 * The precision to use.
	 */
	private int precision;

	/**
	 * The columns to use.
	 */
	private final List<String> columns = new ArrayList<String>();

	/**
	 * The output file.
	 */
	private PrintWriter tw;

	/**
	 * True, if headers are present.
	 */
	private boolean headers;

	/**
	 * The format of the CSV file.
	 */
	private CSVFormat format;

	/**
	 * The output line, as it is built.
	 */
	private StringBuilder line;

	/**
	 * True, if columns were defined.
	 */
	private boolean columnsDefined;

	/**
	 * Construct the object, and set the defaults.
	 */
	public NinjaStreamWriter() {
		this.precision = Encog.DEFAULT_PRECISION;
		this.columnsDefined = false;
	}

	/**
	 * Begin a bar, for the specified date/time.
	 * 
	 * @param dt
	 *            The date/time where the bar begins.
	 */
	public final void beginBar(final Date dt) {
		if (this.tw == null) {
			throw new QuantError("Must open file first.");
		}

		if (this.line != null) {
			throw new QuantError("Must call end bar");
		}

		this.line = new StringBuilder();
		this.line.append(NumericDateUtil.date2Long(dt));
		this.line.append(this.format.getSeparator());
		this.line.append(NumericDateUtil.time2Int(dt));
	}

	/**
	 * Close the file.
	 */
	public final void close() {
		if (this.tw == null) {
			throw new QuantError("Must open file first.");
		}
		this.tw.close();
	}

	/**
	 * End the current bar.
	 */
	public final void endBar() {
		if (this.tw == null) {
			throw new QuantError("Must open file first.");
		}

		if (this.line == null) {
			throw new QuantError("Must call BeginBar first.");
		}

		if (this.headers && !this.columnsDefined) {
			writeHeaders();
		}

		this.tw.println(this.line.toString());
		this.line = null;
		this.columnsDefined = true;
	}

	/**
	 * @return The precision to use.
	 */
	public final int getPrecision() {
		return this.precision;
	}

	/**
	 * Open the file for output.
	 * 
	 * @param filename
	 *            The filename.
	 * @param theHeaders
	 *            True, if headers are present.
	 * @param theFormat
	 *            The CSV format.
	 */
	public final void open(final String filename, final boolean theHeaders,
			final CSVFormat theFormat) {
		try {
			this.tw = new PrintWriter(new FileWriter(filename));
			this.format = theFormat;
			this.headers = theHeaders;
		} catch (final IOException e) {
			throw new QuantError(e);
		}
	}

	/**
	 * Set the percision to use.
	 * 
	 * @param thePrecision
	 *            The percision to use.
	 */
	public final void setPercision(final int thePrecision) {
		this.precision = thePrecision;
	}

	/**
	 * Store a column.
	 * 
	 * @param name
	 *            The name of the column.
	 * @param d
	 *            The value to store.
	 */
	public final void storeColumn(final String name, final double d) {
		if (this.line == null) {
			throw new QuantError("Must call BeginBar first.");
		}

		if (this.line.length() > 0) {
			this.line.append(this.format.getSeparator());
		}

		this.line.append(this.format.format(d, this.precision));

		if (!this.columnsDefined) {
			this.columns.add(name);
		}
	}

	/**
	 * Write the headers.
	 */
	private void writeHeaders() {
		if (this.tw == null) {
			throw new QuantError("Must open file first.");
		}

		final StringBuilder theLine = new StringBuilder();

		theLine.append(FileData.DATE);
		theLine.append(this.format.getSeparator());
		theLine.append(FileData.TIME);

		for (final String str : this.columns) {
			if (theLine.length() > 0) {
				theLine.append(this.format.getSeparator());
			}

			theLine.append("\"");
			theLine.append(str);
			theLine.append("\"");
		}
		this.tw.println(theLine.toString());
	}

}
