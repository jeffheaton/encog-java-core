/*
  * Encog Neural Network and Bot Library for Java v1.x
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * 
  * Copyright 2008, Heaton Research Inc., and individual contributors.
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

package org.encog.neural.data.market;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.encog.neural.data.market.loader.LoadedMarketData;
import org.encog.neural.data.market.loader.MarketLoader;
import org.encog.neural.data.temporal.TemporalDataDescription;
import org.encog.neural.data.temporal.TemporalNeuralDataSet;
import org.encog.neural.data.temporal.TemporalPoint;
import org.encog.util.time.TimeUnit;

public class MarketNeuralDataSet extends TemporalNeuralDataSet {
	
	private MarketLoader loader;
	private Map<Integer,TemporalPoint> pointIndex = new HashMap<Integer,TemporalPoint>();
	
	public MarketNeuralDataSet(MarketLoader loader, int inputWindowSize, int predictWindowSize) {
		super(inputWindowSize, predictWindowSize);
		this.loader = loader;
		setSequenceGrandularity(TimeUnit.DAYS);
	}
	
	public void addDescription(TemporalDataDescription desc)
	{
		if( !(desc instanceof MarketDataDescription) )
			throw new MarketError("Only MarketDataDescription objects may be used with the MarketNeuralDataSet container.");
		super.addDescription(desc);
	}

	public MarketLoader getLoader()
	{
		return this.loader;
	}
	
	public TemporalPoint createPoint(Date when)
	{		
		int sequence = this.getSequenceFromDate(when);
		TemporalPoint result = this.pointIndex.get(sequence);
		
		if( result==null ) {
			result = super.createPoint(when);
			this.pointIndex.put(result.getSequence(), result);
		}
		
		return result;
	}
	
	private void loadPointFromMarketData(TickerSymbol ticker, TemporalPoint point,
			LoadedMarketData item) {
		for( TemporalDataDescription desc: this.getDescriptions())
		{
			MarketDataDescription mdesc = (MarketDataDescription)desc;
			
			if( mdesc.getTicker().equals(ticker))
			{
				point.setData( mdesc.getIndex(), item.getData(mdesc.getDataType()) );
			}
		}		
	}
	
	private void loadSymbol(TickerSymbol ticker,Date from,Date to)
	{
		Collection<LoadedMarketData> data = this.getLoader().load(ticker, null, from, to);
		for(LoadedMarketData item:data )
		{
			TemporalPoint point = this.createPoint(item.getWhen());
			
			loadPointFromMarketData(ticker, point,item);			
		}
	}
	
	public void load(Date begin, Date end)
	{
		// define the starting point if it is not already defined
		if(this.getStartingPoint()==null)
			this.setStartingPoint(begin);
		
		// clear out any loaded points
		getPoints().clear();
		
		// first obtain a collection of symbols that need to be looked up
		Set<TickerSymbol> set = new HashSet<TickerSymbol>();
		for( TemporalDataDescription desc: this.getDescriptions())
		{
			MarketDataDescription mdesc = (MarketDataDescription)desc;
			set.add(mdesc.getTicker());
		}
		
		// now loop over each symbol and load the data
		for(TickerSymbol symbol: set)
		{
			loadSymbol(symbol,begin,end);
		}
		
		// resort the points
		sortPoints();
	}


}
