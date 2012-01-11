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
package org.encog.ml.data.market.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.encog.ml.data.market.MarketDataType;
import org.encog.ml.data.market.TickerSymbol;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.http.FormUtility;

/**
 * This class loads financial data from Yahoo. One caution on Yahoo data.
 * 
 * @author jheaton
 */
public class YahooFinanceLoader implements MarketLoader {

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
	private URL buildURL(final TickerSymbol ticker, final Date from,
			final Date to) throws IOException {
		// process the dates
		final Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(from);
		final Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(to);

		// construct the URL
		final OutputStream os = new ByteArrayOutputStream();
		final FormUtility form = new FormUtility(os, null);
		form.add("s", ticker.getSymbol().toUpperCase());
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
	 * Load the specified financial data.
	 * 
	 * @param ticker
	 *            The ticker symbol to load.
	 * @param dataNeeded
	 *            The financial data needed.
	 * @param from
	 *            The beginning date to load data from.
	 * @param to
	 *            The ending date to load data to.
	 * @return A collection of LoadedMarketData objects that represent the data
	 *         loaded.
	 */
	public Collection<LoadedMarketData> load(final TickerSymbol ticker,
			final Set<MarketDataType> dataNeeded, final Date from, 
			final Date to) {
		try {
			final Collection<LoadedMarketData> result = 
				new ArrayList<LoadedMarketData>();
			final URL url = buildURL(ticker, from, to);
			final InputStream is = url.openStream();
			final ReadCSV csv = new ReadCSV(is, true, CSVFormat.ENGLISH);

			while (csv.next()) {
				final Date date = csv.getDate("date");
				final double adjClose = csv.getDouble("adj close");
				final double open = csv.getDouble("open");
				final double close = csv.getDouble("close");
				final double high = csv.getDouble("high");
				final double low = csv.getDouble("low");
				final double volume = csv.getDouble("volume");

				final LoadedMarketData data = 
					new LoadedMarketData(date, ticker);
				data.setData(MarketDataType.ADJUSTED_CLOSE, adjClose);
				data.setData(MarketDataType.OPEN, open);
				data.setData(MarketDataType.CLOSE, close);
				data.setData(MarketDataType.HIGH, high);
				data.setData(MarketDataType.LOW, low);
				data.setData(MarketDataType.OPEN, open);
				data.setData(MarketDataType.VOLUME, volume);
				result.add(data);
			}

			csv.close();
			is.close();
			return result;
		} catch (final IOException e) {
			throw new LoaderError(e);
		}
	}
}
