/*
 * Encog(tm) Core v2.6 Unit Test - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
