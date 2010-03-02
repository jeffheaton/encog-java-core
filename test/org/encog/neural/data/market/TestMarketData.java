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

package org.encog.neural.data.market;

import java.util.Date;
import java.util.Iterator;

import junit.framework.TestCase;

import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.market.loader.MarketLoader;
import org.encog.neural.data.market.loader.YahooFinanceLoader;
import org.encog.neural.data.temporal.TemporalPoint;
import org.encog.util.time.DateUtil;

public class TestMarketData extends TestCase {
	
	
	public void testMarketData() throws Exception
	{
		MarketLoader loader = new YahooFinanceLoader();
		TickerSymbol tickerAAPL = new TickerSymbol("AAPL",null);
		TickerSymbol tickerMSFT = new TickerSymbol("MSFT",null);
		MarketNeuralDataSet marketData = new MarketNeuralDataSet(loader, 5,1);
		marketData.addDescription(new MarketDataDescription(tickerAAPL,MarketDataType.CLOSE, true, true));
		marketData.addDescription(new MarketDataDescription(tickerMSFT,MarketDataType.CLOSE, true, false));		
		marketData.addDescription(new MarketDataDescription(tickerAAPL,MarketDataType.VOLUME, true, false));
		marketData.addDescription(new MarketDataDescription(tickerMSFT,MarketDataType.VOLUME, true, false));		
		Date begin = DateUtil.createDate(7, 1, 2008);
		Date end = DateUtil.createDate(7, 31, 2008);
		marketData.load(begin, end);
		marketData.generate();
		TestCase.assertEquals(22,marketData.getPoints().size());
		
		// first test the points
		Iterator<TemporalPoint> itr = marketData.getPoints().iterator();
		TemporalPoint point = itr.next();
		TestCase.assertEquals(0, point.getSequence());
		TestCase.assertEquals(4, point.getData().length);
		TestCase.assertEquals(174.68,point.getData(0));
		TestCase.assertEquals(26.87,point.getData(1));
		TestCase.assertEquals(39,(int)(point.getData(2)/1000000));
		TestCase.assertEquals(100,(int)(point.getData(3)/1000000));
		
		point = itr.next();
		TestCase.assertEquals(1, point.getSequence());
		TestCase.assertEquals(4, point.getData().length);
		TestCase.assertEquals(168.18,point.getData(0));
		TestCase.assertEquals(25.88,point.getData(1));
		TestCase.assertEquals(29,(int)(point.getData(2)/1000000));
		TestCase.assertEquals(84,(int)(point.getData(3)/1000000));
		
		point = itr.next();
		TestCase.assertEquals(2, point.getSequence());
		TestCase.assertEquals(4, point.getData().length);
		TestCase.assertEquals(170.12,point.getData(0));
		TestCase.assertEquals(25.98,point.getData(1));
		TestCase.assertEquals(18,(int)(point.getData(2)/1000000));
		TestCase.assertEquals(37,(int)(point.getData(3)/1000000));

		// now check the actual data
		TestCase.assertEquals(16,marketData.getData().size());
		TestCase.assertEquals(20, marketData.getInputNeuronCount());
		TestCase.assertEquals(1, marketData.getOutputNeuronCount());

		Iterator<NeuralDataPair> itr2 = marketData.getData().iterator();
		NeuralDataPair pair = itr2.next();
		TestCase.assertEquals(20, pair.getInput().size());
		TestCase.assertEquals(1, pair.getIdeal().size());
		
		TestCase.assertEquals(-0.037, Math.round(pair.getInput().getData(0)*1000.0)/1000.0);
		TestCase.assertEquals(-0.037, Math.round(pair.getInput().getData(1)*1000.0)/1000.0);		
		TestCase.assertEquals(-0.246, Math.round(pair.getInput().getData(2)*1000.0)/1000.0);
		TestCase.assertEquals(-0.156, Math.round(pair.getInput().getData(3)*1000.0)/1000.0);
		TestCase.assertEquals(0.012, Math.round(pair.getInput().getData(4)*1000.0)/1000.0);
		TestCase.assertEquals(0.0040, Math.round(pair.getInput().getData(5)*1000.0)/1000.0);
		TestCase.assertEquals(-0.375, Math.round(pair.getInput().getData(6)*1000.0)/1000.0);
		TestCase.assertEquals(-0.562, Math.round(pair.getInput().getData(7)*1000.0)/1000.0);
		TestCase.assertEquals(0.03, Math.round(pair.getInput().getData(8)*1000.0)/1000.0);
		TestCase.assertEquals(0.0020, Math.round(pair.getInput().getData(9)*1000.0)/1000.0);
		TestCase.assertEquals(0.57, Math.round(pair.getInput().getData(10)*100.0)/100.0);
		TestCase.assertEquals(0.929, Math.round(pair.getInput().getData(11)*1000.0)/1000.0);
		TestCase.assertEquals(0.025, Math.round(pair.getInput().getData(12)*1000.0)/1000.0);
		TestCase.assertEquals(-0.0070, Math.round(pair.getInput().getData(13)*1000.0)/1000.0);
		// for some reason, Yahoo likes to vary the volume numbers slightly, sometimes!
		TestCase.assertEquals(0.1, Math.round(pair.getInput().getData(14)*10.0)/10.0);
		TestCase.assertEquals(-0.084, Math.round(pair.getInput().getData(15)*1000.0)/1000.0);
		TestCase.assertEquals(-0.03, Math.round(pair.getInput().getData(16)*1000.0)/1000.0);
		TestCase.assertEquals(-0.024, Math.round(pair.getInput().getData(17)*1000.0)/1000.0);
		TestCase.assertEquals(0.008, Math.round(pair.getInput().getData(18)*1000.0)/1000.0);
		TestCase.assertEquals(-0.172, Math.round(pair.getInput().getData(19)*1000.0)/1000.0);

		pair = itr2.next();
		TestCase.assertEquals(20, pair.getInput().size());
		TestCase.assertEquals(1, pair.getIdeal().size());

		TestCase.assertEquals(0.012, Math.round(pair.getInput().getData(0)*1000.0)/1000.0);
		TestCase.assertEquals(0.0040, Math.round(pair.getInput().getData(1)*1000.0)/1000.0);
		TestCase.assertEquals(-0.375, Math.round(pair.getInput().getData(2)*1000.0)/1000.0);
		TestCase.assertEquals(-0.562, Math.round(pair.getInput().getData(3)*1000.0)/1000.0);
		TestCase.assertEquals(0.03, Math.round(pair.getInput().getData(4)*1000.0)/1000.0);
		TestCase.assertEquals(0.0020, Math.round(pair.getInput().getData(5)*1000.0)/1000.0);
		TestCase.assertEquals(0.6, Math.round(pair.getInput().getData(6)*10.0)/10.0);
		TestCase.assertEquals(0.929, Math.round(pair.getInput().getData(7)*1000.0)/1000.0);
		TestCase.assertEquals(0.025, Math.round(pair.getInput().getData(8)*1000.0)/1000.0);
		TestCase.assertEquals(-0.0070, Math.round(pair.getInput().getData(9)*1000.0)/1000.0);
		TestCase.assertEquals(0.1, Math.round(pair.getInput().getData(10)*10.0)/10.0);
		TestCase.assertEquals(-0.084, Math.round(pair.getInput().getData(11)*1000.0)/1000.0);
		TestCase.assertEquals(-0.03, Math.round(pair.getInput().getData(12)*1000.0)/1000.0);
		TestCase.assertEquals(-0.024, Math.round(pair.getInput().getData(13)*1000.0)/1000.0);
		TestCase.assertEquals(0.0080, Math.round(pair.getInput().getData(14)*1000.0)/1000.0);
		TestCase.assertEquals(-0.172, Math.round(pair.getInput().getData(15)*1000.0)/1000.0);		
		TestCase.assertEquals(0.014, Math.round(pair.getInput().getData(16)*1000.0)/1000.0);
		TestCase.assertEquals(0.0090, Math.round(pair.getInput().getData(17)*1000.0)/1000.0);
		TestCase.assertEquals(-0.1, Math.round(pair.getInput().getData(18)*10.0)/10.0);
		TestCase.assertEquals(0.066, Math.round(pair.getInput().getData(19)*1000.0)/1000.0);
		

		
	}
}
