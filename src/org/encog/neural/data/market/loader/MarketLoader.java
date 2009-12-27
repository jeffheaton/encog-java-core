/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
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

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.encog.neural.data.market.MarketDataType;
import org.encog.neural.data.market.TickerSymbol;

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
