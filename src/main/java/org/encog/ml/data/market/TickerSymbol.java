/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.ml.data.market;

import org.encog.util.obj.ReflectionUtil;

/**
 * Holds a ticker symbol and exchange. The exchange is for external use only and
 * is not used by Encog currently.
 * 
 * @author jheaton
 * 
 */
public class TickerSymbol {

	/**
	 * The ticker symbol.
	 */
	private final String symbol;

	/**
	 * The exchange.
	 */
	private final String exchange;

	/**
	 * Construct a ticker symbol with no exchange.
	 * 
	 * @param symbol
	 *            The ticker symbol.
	 */
	public TickerSymbol(final String symbol) {
		this.symbol = symbol;
		this.exchange = null;
	}

	/**
	 * Construct a ticker symbol with exchange.
	 * 
	 * @param symbol
	 *            The ticker symbol.
	 * @param exchange
	 *            The exchange.
	 */
	public TickerSymbol(final String symbol, final String exchange) {
		this.symbol = symbol;
		this.exchange = exchange;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(final Object other) {

	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof TickerSymbol))return false;
	    TickerSymbol otherMyClass = (TickerSymbol)other;		

		// if the symbols do not even match then they are not equal
		if (!otherMyClass.getSymbol().equals(getSymbol())) {
			return false;
		}

		// if the symbols match then we need to compare the exchanges
		if ((otherMyClass.getExchange() == null) && (otherMyClass.getExchange() == null)) {
			return true;
		}

		if ((otherMyClass.getExchange() == null) || (otherMyClass.getExchange() == null)) {
			return false;
		}

		return otherMyClass.getExchange().equals(getExchange());
	}

	/**
	 * @return the exchange
	 */
	public String getExchange() {
		return this.exchange;
	}

	/**
	 * @return the symbol
	 */
	public String getSymbol() {
		return this.symbol;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return ReflectionUtil.safeHashCode(this.symbol)
				+ ReflectionUtil.safeHashCode(this.exchange);
	}
}
