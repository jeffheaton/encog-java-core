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
package org.encog.ml.data.market;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.encog.ml.data.market.loader.LoadedMarketData;
import org.encog.ml.data.market.loader.MarketLoader;
import org.encog.ml.data.temporal.TemporalDataDescription;
import org.encog.ml.data.temporal.TemporalMLDataSet;
import org.encog.ml.data.temporal.TemporalPoint;
import org.encog.neural.data.NeuralData;
import org.encog.util.time.TimeUnit;

/**
 * A data set that is designed to hold market data. This class is based on the
 * TemporalNeuralDataSet. This class is designed to load financial data from
 * external sources. This class is designed to track financial data across days.
 * However, it should be usable with other levels of granularity as well.
 * 
 * @author jheaton
 * 
 */
public class MarketMLDataSet extends TemporalMLDataSet {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 170791819906003867L;

	/**
	 * The loader to use to obtain the data.
	 */
	private final MarketLoader loader;

	/**
	 * A map between the data points and actual data.
	 */
	private final Map<Integer, TemporalPoint> pointIndex = 
		new HashMap<Integer, TemporalPoint>();

	/**
	 * Construct a market data set object.
	 * 
	 * @param loader
	 *            The loader to use to get the financial data.
	 * @param inputWindowSize
	 *            The input window size, that is how many datapoints do we use
	 *            to predict.
	 * @param predictWindowSize
	 *            How many datapoints do we want to predict.
	 */
	public MarketMLDataSet(final MarketLoader loader,
			final int inputWindowSize, final int predictWindowSize) {
		super(inputWindowSize, predictWindowSize);
		this.loader = loader;
		setSequenceGrandularity(TimeUnit.DAYS);
	}

	/**
	 * Add one description of the type of market data that we are seeking at
	 * each datapoint.
	 * 
	 * @param desc
	 *            The data description.
	 */
	@Override
	public void addDescription(final TemporalDataDescription desc) {
		if (!(desc instanceof MarketDataDescription)) {

			final String str = "Only MarketDataDescription objects may be used "
					+ "with the MarketNeuralDataSet container.";

			throw new MarketError(str);
		}
		super.addDescription(desc);
	}

	/**
	 * Create a datapoint at the specified date.
	 * 
	 * @param when
	 *            The date to create the point at.
	 * @return Returns the TemporalPoint created for the specified date.
	 */
	@Override
	public TemporalPoint createPoint(final Date when) {
		final int sequence = getSequenceFromDate(when);
		TemporalPoint result = this.pointIndex.get(sequence);

		if (result == null) {
			result = super.createPoint(when);
			this.pointIndex.put(result.getSequence(), result);
		}

		return result;
	}

	/**
	 * To be implemented later.
	 * 
	 * @param date
	 *            NOT USED
	 * @return NOT USED
	 */
	public NeuralData generateInputForPrediction(final Date date) {
		return null;
	}

	/**
	 * @return The loader that is being used for this set.
	 */
	public MarketLoader getLoader() {
		return this.loader;
	}

	/**
	 * Load data from the loader.
	 * 
	 * @param begin
	 *            The beginning date.
	 * @param end
	 *            The ending date.
	 */
	public void load(final Date begin, final Date end) {
		// define the starting point if it is not already defined
		if (getStartingPoint() == null) {
			setStartingPoint(begin);
		}

		// clear out any loaded points
		getPoints().clear();

		// first obtain a collection of symbols that need to be looked up
		final Set<TickerSymbol> set = new HashSet<TickerSymbol>();
		for (final TemporalDataDescription desc : getDescriptions()) {
			final MarketDataDescription mdesc = (MarketDataDescription) desc;
			set.add(mdesc.getTicker());
		}

		// now loop over each symbol and load the data
		for (final TickerSymbol symbol : set) {
			loadSymbol(symbol, begin, end);
		}

		// resort the points
		sortPoints();
	}

	/**
	 * Load one point of market data.
	 * 
	 * @param ticker
	 *            The ticker symbol to load.
	 * @param point
	 *            The point to load at.
	 * @param item
	 *            The item being loaded.
	 */
	private void loadPointFromMarketData(final TickerSymbol ticker,
			final TemporalPoint point, final LoadedMarketData item) {
		for (final TemporalDataDescription desc : getDescriptions()) {
			final MarketDataDescription mdesc = (MarketDataDescription) desc;

			if (mdesc.getTicker().equals(ticker)) {
				point.setData(mdesc.getIndex(), item.getData(mdesc
						.getDataType()));
			}
		}
	}

	/**
	 * Load one ticker symbol.
	 * 
	 * @param ticker
	 *            The ticker symbol to load.
	 * @param from
	 *            Load data from this date.
	 * @param to
	 *            Load data to this date.
	 */
	private void loadSymbol(final TickerSymbol ticker, final Date from,
			final Date to) {
		final Collection<LoadedMarketData> data = getLoader().load(ticker,
				null, from, to);
		for (final LoadedMarketData item : data) {
			final TemporalPoint point = this.createPoint(item.getWhen());

			loadPointFromMarketData(ticker, point, item);
		}
	}

}
