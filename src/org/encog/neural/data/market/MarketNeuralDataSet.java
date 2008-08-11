package org.encog.neural.data.market;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
		if( desc instanceof MarketDataDescription )
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
		
		if( result==null )
			result = super.createPoint(when);
		
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
	}


}
