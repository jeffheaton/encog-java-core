/*
 * Encog Artificial Intelligence Framework v1.x
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

package org.encog.neural.data.market.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.encog.bot.html.FormUtility;
import org.encog.neural.data.market.MarketDataType;
import org.encog.neural.data.market.TickerSymbol;
import org.encog.util.ReadCSV;

/**
 * This class loads financial data from Yahoo.  One caution on 
 * Yahoo data.  I've noticed that the volume numbers will
 * fluxuate some from one load to the next.  It is generally
 * by a very small percent, but it was enough to force me to
 * adjust the unit tests some.
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
			final ReadCSV csv = new ReadCSV(is, true, ',');

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
		} catch (final ParseException e) {
			throw new LoaderError(e);
		}
	}
}
