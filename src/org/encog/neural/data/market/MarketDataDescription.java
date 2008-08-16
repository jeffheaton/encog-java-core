package org.encog.neural.data.market;

import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.data.temporal.TemporalDataDescription;

public class MarketDataDescription extends TemporalDataDescription {

	private TickerSymbol ticker;
	MarketDataType dataType;
	
	public MarketDataDescription(TickerSymbol ticker, MarketDataType dataType, ActivationFunction activationFunction, boolean input, boolean predict) {
		super(activationFunction, Type.PERCENT_CHANGE, input, predict);
		this.ticker = ticker;
		this.dataType = dataType;
	}
	
	public MarketDataDescription(TickerSymbol ticker, MarketDataType dataType, boolean input, boolean predict)
	{
		this(ticker,dataType,null,input,predict);
	}

	/**
	 * @return the ticker
	 */
	public TickerSymbol getTicker() {
		return ticker;
	}

	/**
	 * @return the dataType
	 */
	public MarketDataType getDataType() {
		return dataType;
	}
	
	

}
