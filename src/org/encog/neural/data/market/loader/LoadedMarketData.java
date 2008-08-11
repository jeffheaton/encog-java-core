package org.encog.neural.data.market.loader;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.encog.neural.data.market.TickerSymbol;
import org.encog.neural.data.market.MarketDataType;

public class LoadedMarketData {
	private Date when;
	private TickerSymbol ticker;
	private Map<MarketDataType,Double> data;
	
	public LoadedMarketData(Date when,TickerSymbol ticker)
	{
		this.when = when;
		this.ticker = ticker;
		data = new HashMap<MarketDataType,Double>();
	}
	
	public Date getWhen()
	{
		return this.when;
	}
	
	public TickerSymbol getTicker()
	{
		return this.ticker;
	}
	
	public double getData(MarketDataType type)
	{
		return this.data.get(type);
	}
	
	public void setData(MarketDataType type, double data)
	{
		this.data.put(type, data);
	}
}
