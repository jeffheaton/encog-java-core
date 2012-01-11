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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.encog.ml.data.market.MarketDataType;
import org.encog.ml.data.market.TickerSymbol;

/**
 * This class contains market data that was loaded for a specific ticker symbol
 * and a specific date. This data is usually loaded from external sources.
 *
 * @author jheaton
 */
public class LoadedMarketData implements Comparable<LoadedMarketData> {

	public final int INDEX_DOUBLE_HIGH = 0;
	public final int INDEX_DOUBLE_LOW = 0;
	public final int INDEX_DOUBLE_OPEN = 0;
	public final int INDEX_DOUBLE_CLOSE = 0;
	
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
