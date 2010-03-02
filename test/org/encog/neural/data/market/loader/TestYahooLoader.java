/*
 * Encog(tm) Core v2.4
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
