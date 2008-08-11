package org.encog.neural.data.market.loader;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.encog.neural.data.market.MarketDataType;
import org.encog.neural.data.market.TickerSymbol;
import org.encog.util.time.DateUtil;

import junit.framework.TestCase;

public class TestYahooLoader extends TestCase {
	public void testLoader() throws Exception 
	{
		MarketLoader loader = new YahooFinanceLoader();
		Date from = DateUtil.createDate(8, 4, 2008);
		Date to = DateUtil.createDate(8, 5, 2008);
		Collection<LoadedMarketData> list = loader.load(new TickerSymbol("aapl"), null, from, to);
		TestCase.assertEquals(2, list.size());
		Iterator<LoadedMarketData> itr = list.iterator();
		
		LoadedMarketData data = itr.next();
		TestCase.assertEquals(160.64, data.getData(MarketDataType.CLOSE));
		data = itr.next();
		TestCase.assertEquals(153.23, data.getData(MarketDataType.CLOSE));
		
		
	}
}
