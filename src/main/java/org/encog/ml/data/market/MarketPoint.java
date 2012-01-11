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

import java.util.Date;

import org.encog.ml.data.temporal.TemporalPoint;

/**
 * Hold one market datapoint. This class is based on the TemporalPoint, however
 * it is designed to take its sequence number from a date.
 * 
 * @author jheaton
 * 
 */
public class MarketPoint extends TemporalPoint {

	/**
	 * When to hold the data from.
	 */
	private final Date when;

	/**
	 * Construct a MarketPoint with the specified date and size.
	 * 
	 * @param when
	 *            When is this data from.
	 * @param size
	 *            What is the size of the data.
	 */
	public MarketPoint(final Date when, final int size) {
		super(size);
		this.when = when;
	}

	/**
	 * @return the when
	 */
	public Date getWhen() {
		return this.when;
	}

}
