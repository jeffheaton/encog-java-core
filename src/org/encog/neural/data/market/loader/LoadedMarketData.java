/*
 * Encog(tm) Core v2.5
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

package org.encog.neural.data.market.loader;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.encog.neural.data.market.TickerSymbol;
import org.encog.neural.data.market.MarketDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains market data that was loaded for a specific ticker symbol
 * and a specific date. This data is usually loaded from external sources.
 *
 * @author jheaton
 */
public class LoadedMarketData implements Comparable<LoadedMarketData> {

	/**
	 * When was this data sample taken.
	 */
	private final Date when;

	/**
	 * What is the ticker symbol for this data sample.
	 */
	private final TickerSymbol ticker;

	/**
	 * The data that was collection for the sample date.
	 */
	private final Map<MarketDataType, Double> data;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct one sample of market data.
	 *
	 * @param when
	 *            When was this sample taken.
	 * @param ticker
	 *            What is the ticker symbol for this data.
	 */
	public LoadedMarketData(final Date when, final TickerSymbol ticker) {
		this.when = when;
		this.ticker = ticker;
		this.data = new HashMap<MarketDataType, Double>();
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(final LoadedMarketData other) {
		return getWhen().compareTo(other.getWhen());
	}

	/**
	 * Get one type of market data from this date.
	 *
	 * @param type
	 *            The type of data needed.
	 * @return The market data for the specified date and of the specified type.
	 */
	public double getData(final MarketDataType type) {
		return this.data.get(type);
	}

	/**
	 * @return The ticker symbol this sample is assocated with.
	 */
	public TickerSymbol getTicker() {
		return this.ticker;
	}

	/**
	 * @return When this sample was taken.
	 */
	public Date getWhen() {
		return this.when;
	}

	/**
	 * Set financial data for this date.
	 *
	 * @param type
	 *            The type of data being set.
	 * @param data
	 *            The value of the data being set.
	 */
	public void setData(final MarketDataType type, final double data) {
		this.data.put(type, data);
	}
}
