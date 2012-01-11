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
package org.encog.app.quant.loader.yahoo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

import org.encog.Encog;
import org.encog.app.quant.QuantTask;
import org.encog.app.quant.loader.LoaderError;
import org.encog.app.quant.loader.MarketLoader;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.http.FormUtility;
import org.encog.util.time.NumericDateUtil;

/**
 * Download financial data from Yahoo.
 */
public class YahooDownload implements MarketLoader, QuantTask {

	/**
	 * The Dow Jones Industrial Average.
	 */
	public static final String INDEX_DJIA = "^dji";
	
	/**
	 * The S&P 500.
	 */
	public static final String INDEX_SP500 = "^gspc";
	
	/**
	 * The NASDAQ.
	 */
	public static final String INDEX_NASDAQ = "^ixic";

	/**
	 * The precision to use.
	 */
	private int precision;
	
	/**
	 * True, if we were canceled.
	 */
	private boolean cancel;

	/**
	 * Construct the object with default precision.
	 */
	public YahooDownload() {
		setPercision(Encog.DEFAULT_PRECISION);
	}

	/**
	 * This method builds a URL to load data from Yahoo Finance for a neural
	 * network to train with.
	 * 
	 * @param ticker
	 *            The ticker symbol to access.
	 * @param from
	 *            The beginning date.
	 * @param to
	 *            The ending date.
	 * @return The UEL
	 * @throws IOException
	 *             An error accessing the data.
	 */
	private URL buildURL(final String ticker, final Date from, final Date to)
			throws IOException {
		// process the dates
		final Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(from);
		final Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(to);

		// construct the URL
		final OutputStream os = new ByteArrayOutputStream();
		final FormUtility form = new FormUtility(os, null);
		form.add("s", ticker.toUpperCase());
		form.add("a", "" + calendarFrom.get(Calendar.MONTH));
		form.add("b", "" + calendarFrom.get(Calendar.DAY_OF_MONTH));
		form.add("c", "" + calendarFrom.get(Calendar.YEAR));
		form.add("d", "" + calendarTo.get(Calendar.MONTH));
		form.add("e", "" + calendarTo.get(Calendar.DAY_OF_MONTH));
		form.add("f", "" + calendarTo.get(Calendar.YEAR));
		form.add("g", "d");
		form.add("ignore", ".csv");
		os.close();
		final String str = "http://ichart.finance.yahoo.com/table.csv?"
				+ os.toString();
		return new URL(str);
	}

	/**
	 * @return the precision.
	 */
	public final int getPrecision() {
		return this.precision;
	}

	/**
	 * Load all data.
	 * @param ticker The ticker.
	 * @param output The output file.
	 * @param outputFormat The format of the output file.
	 * @param from Starting date.
	 * @param to Ending date.
	 */
	public final void loadAllData(final String ticker, 
			final File output,
			final CSVFormat outputFormat, final Date from, final Date to) {
		try {
			final URL url = buildURL(ticker, from, to);
			final InputStream is = url.openStream();
			final ReadCSV csv = new ReadCSV(is, true, CSVFormat.ENGLISH);

			final PrintWriter tw = new PrintWriter(new FileWriter(output));
			tw.println(
		"date,time,open price,high price,low price," 
					+ "close price,volume,adjusted price");

			while (csv.next() && !shouldStop()) {
				final Date date = csv.getDate("date");
				final double adjClose = csv.getDouble("adj close");
				final double open = csv.getDouble("open");
				final double close = csv.getDouble("close");
				final double high = csv.getDouble("high");
				final double low = csv.getDouble("low");
				final double volume = csv.getDouble("volume");

				final NumberFormat df = NumberFormat.getInstance();
				df.setGroupingUsed(false);

				final StringBuilder line = new StringBuilder();
				line.append(NumericDateUtil.date2Long(date));
				line.append(outputFormat.getSeparator());
				line.append(NumericDateUtil.time2Int(date));
				line.append(outputFormat.getSeparator());
				line.append(outputFormat.format(open, this.precision));
				line.append(outputFormat.getSeparator());
				line.append(outputFormat.format(high, this.precision));
				line.append(outputFormat.getSeparator());
				line.append(outputFormat.format(low, this.precision));
				line.append(outputFormat.getSeparator());
				line.append(outputFormat.format(close, this.precision));
				line.append(outputFormat.getSeparator());
				line.append(df.format(volume));
				line.append(outputFormat.getSeparator());
				line.append(outputFormat.format(adjClose, this.precision));
				tw.println(line.toString());
			}

			tw.close();
		} catch (final IOException ex) {
			throw new LoaderError(ex);
		}
	}

	/**
	 * Request to stop.
	 */
	@Override
	public final void requestStop() {
		this.cancel = true;
	}

	/**
	 * @param thePrecision
	 *            the precision to set
	 */
	public final void setPercision(final int thePrecision) {
		this.precision = thePrecision;
	}

	/**
	 * @return True, if we should stop.
	 */
	@Override
	public final boolean shouldStop() {
		return this.cancel;
	}

}
