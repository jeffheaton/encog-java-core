package org.encog.neural.data.market.loader;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.encog.neural.data.market.MarketDataType;
import org.encog.neural.data.market.TickerSymbol;

public interface MarketLoader {
	public Collection<LoadedMarketData> load(TickerSymbol ticker,Set<MarketDataType> dataNeeded,Date from, Date to);
}
