/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.data.temporal.TemporalDataDescription;
import org.encog.ml.data.temporal.TemporalDataDescription.Type;

/**
 * This class is used to describe the type of financial data that is needed.
 * Each piece of data can be used for input, prediction or both. If used for
 * input, it will be used as data to help predict. If used for prediction, it
 * will be one of the values predicted. It is possible, and quite common, to use
 * data from both input and prediction.
 * 
 * @author jheaton
 * 
 */
public class MarketDataDescription extends TemporalDataDescription {

	/**
	 * The ticker symbol to be loaded.
	 */
	private final TickerSymbol ticker;

	/**
	 * The type of data to be loaded from the specified ticker symbol.
	 */
	private final MarketDataType dataType;


	/**
	 * Construct a MarketDataDescription item.
	 * 
	 * @param ticker
	 *            The ticker symbol to use.
	 * @param dataType
	 *            The data type needed.
	 * @param type
	 * 			  The normalization type.
	 * @param activationFunction
	 *            The activation function to apply to this data, can be null.
	 * @param input
	 *            Is this field used for input?
	 * @param predict
	 *            Is this field used for prediction?
	 */
	public MarketDataDescription(final TickerSymbol ticker,
			final MarketDataType dataType, final Type type,
			final ActivationFunction activationFunction, final boolean input,
			final boolean predict) {
		super(activationFunction, type, input, predict);
		this.ticker = ticker;
		this.dataType = dataType;
	}

	/**
	 * Construct a MarketDataDescription item.
	 * 
	 * @param ticker
	 *            The ticker symbol to use.
	 * @param dataType
	 *            The data type needed.
	 * @param type
	 * 			  The normalization type.
	 * @param input
	 *            Is this field used for input?
	 * @param predict
	 *            Is this field used for prediction?
	 */
	public MarketDataDescription(final TickerSymbol ticker,
			final MarketDataType dataType, final Type type, final boolean input,
			final boolean predict) {
		this(ticker, dataType, type, null, input, predict);
	}
	
	/**
	 * Construct a MarketDataDescription item.
	 * 
	 * @param ticker
	 *            The ticker symbol to use.
	 * @param dataType
	 *            The data type needed.
	 * @param input
	 *            Is this field used for input?
	 * @param predict
	 *            Is this field used for prediction?
	 */
	public MarketDataDescription(final TickerSymbol ticker,
			final MarketDataType dataType, final boolean input,
			final boolean predict) {
		this(ticker, dataType, Type.PERCENT_CHANGE, null, input, predict);
	}

	/**
	 * @return the dataType
	 */
	public MarketDataType getDataType() {
		return this.dataType;
	}

	/**
	 * @return the ticker
	 */
	public TickerSymbol getTicker() {
		return this.ticker;
	}

}
