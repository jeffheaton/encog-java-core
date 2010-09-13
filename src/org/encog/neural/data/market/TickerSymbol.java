/*
 * Encog(tm) Core v2.5
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

import org.encog.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
	public boolean equals(final Object o) {

		if (!(o instanceof TickerSymbol)) {
			return false;
		}

		TickerSymbol other = (TickerSymbol) o;

		// if the symbols do not even match then they are not equal
		if (!other.getSymbol().equals(getSymbol())) {
			return false;
		}

		// if the symbols match then we need to compare the exchanges
		if ((other.getExchange() == null) && (other.getExchange() == null)) {
			return true;
		}

		if ((other.getExchange() == null) || (other.getExchange() == null)) {
			return false;
		}

		return other.getExchange().equals(getExchange());
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
