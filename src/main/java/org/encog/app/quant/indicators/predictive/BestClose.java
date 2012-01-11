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
package org.encog.app.quant.indicators.predictive;

import java.util.Map;

import org.encog.app.analyst.csv.basic.BaseCachedColumn;
import org.encog.app.analyst.csv.basic.FileData;
import org.encog.app.quant.indicators.Indicator;

/**
 * Get the best close.
 */
public class BestClose extends Indicator {

	/**
	 * The name of this indicator.
	 */
	public static final String NAME = "PredictBestClose";

	/**
	 * The number of periods this indicator is for.
	 */
	private final int periods;

	/**
	 * Construct the object.
	 * 
	 * @param thePeriods
	 *            The number of periods.
	 * @param output
	 *            True, if this indicator is to be predicted.
	 */
	public BestClose(final int thePeriods, final boolean output) {
		super(BestClose.NAME, false, output);
		this.periods = thePeriods;
		setOutput(output);
	}

	/**
	 * Calculate the indicator.
	 * 
	 * @param data
	 *            The data available to the indicator.
	 * @param length
	 *            The length available to the indicator.
	 */
	@Override
	public final void calculate(final Map<String, BaseCachedColumn> data,
			final int length) {
		final double[] close = data.get(FileData.CLOSE).getData();
		final double[] output = getData();

		final int stop = length - this.periods;
		for (int i = 0; i < stop; i++) {
			double bestClose = Double.MIN_VALUE;
			for (int j = 1; j <= this.periods; j++) {
				bestClose = Math.max(close[i + j], bestClose);
			}
			output[i] = bestClose;
		}

		for (int i = length - this.periods; i < length; i++) {
			output[i] = 0;
		}

		setBeginningIndex(0);
		setEndingIndex(length - this.periods - 1);
	}

	/**
	 * @return The number of periods.
	 */
	@Override
	public final int getPeriods() {
		return this.periods;
	}
}
