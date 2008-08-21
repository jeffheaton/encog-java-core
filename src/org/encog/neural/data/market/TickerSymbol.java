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
