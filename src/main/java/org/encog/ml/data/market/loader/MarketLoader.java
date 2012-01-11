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

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.encog.ml.data.market.MarketDataType;
import org.encog.ml.data.market.TickerSymbol;

/**
 * This interface defines a class that can be used to load external financial
 * data.
 * 
 * @author jheaton
 * 
 */
public interface MarketLoader {

	/**
	 * Load the specified ticker symbol for the specified date.
	 * 
	 * @param ticker
	 *            The ticker symbol to load.
	 * @param dataNeeded
	 *            Which data is actually needed.
	 * @param from
	 *            Beginning date for load.
	 * @param to
	 *            Ending date for load.
	 * @return A collection of LoadedMarketData objects that was loaded.
	 */
	Collection<LoadedMarketData> load(TickerSymbol ticker,
			Set<MarketDataType> dataNeeded, Date from, Date to);
}
