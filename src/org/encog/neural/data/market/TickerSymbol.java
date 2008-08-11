package org.encog.neural.data.market;

public class TickerSymbol  {
	private String symbol;
	private String exchange;
	
	public TickerSymbol(String symbol,String exchange)
	{
		this.symbol = symbol;
		this.exchange = exchange;
	}

	public TickerSymbol(String symbol) {
		this.symbol = symbol;
		this.exchange = null;
	}

	/**
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * @return the exchange
	 */
	public String getExchange() {
		return exchange;
	}
	
	public boolean equals(TickerSymbol other)
	{
		// if the symbols do not even match then they are not equal
		if( !other.getSymbol().equals(this.getSymbol()) )
			return false;
		
		// if the symbols match then we need to compare the exchanges 
		if( other.getExchange()==null && other.getExchange()==null )
			return true;
		
		if( other.getExchange()==null || other.getExchange()==null )
			return false;
		
		return other.getExchange().equals(this.getExchange());		
	}
	
	public int hashCode()
	{
		StringBuilder str = new StringBuilder(this.getSymbol());
		if( this.exchange!=null )
			str.append(this.exchange);
		return str.hashCode();
	}
	
}
