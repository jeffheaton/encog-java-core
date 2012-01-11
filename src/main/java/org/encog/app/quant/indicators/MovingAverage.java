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
package org.encog.app.quant.indicators;

import java.util.Map;

import org.encog.app.analyst.csv.basic.BaseCachedColumn;
import org.encog.app.analyst.csv.basic.FileData;

/**
 * A simple moving average.
 */
public class MovingAverage extends Indicator {

	/**
	 * The name of this indicator.
	 */
	public static final String NAME = "MovAvg";

	/**
	 * The number of periods in this indicator.
	 */
	private final int periods;

	/**
	 * Construct this object.
	 * 
	 * @param thePeriods
	 *            The number of periods in this indicator.
	 * @param output
	 *            True, if this indicator is predicted.
	 */
	public MovingAverage(final int thePeriods, final boolean output) {
		super(MovingAverage.NAME, false, output);
		this.periods = thePeriods;
		setOutput(output);
	}

	/**
	 * Calculate this indicator.
	 * 
	 * @param data
	 *            The data to use.
	 * @param length
	 *            The length to calculate over.
	 */
	@Override
	public final void calculate(final Map<String, BaseCachedColumn> data,
			final int length) {
		require(data, FileData.CLOSE);

		final double[] close = data.get(FileData.CLOSE).getData();
		final double[] output = getData();

		final int lookbackTotal = (this.periods - 1);

		final int start = lookbackTotal;
		if (start > (this.periods - 1)) {
			return;
		}

		double periodTotal = 0;
		int trailingIdx = start - lookbackTotal;
		int i = trailingIdx;
		if (this.periods > 1) {
			while (i < start) {
				periodTotal += close[i++];
			}
		}

		int outIdx = this.periods - 1;
		do {
			periodTotal += close[i++];
			final double t = periodTotal;
			periodTotal -= close[trailingIdx++];
			output[outIdx++] = t / this.periods;
		} while (i < close.length);

		setBeginningIndex(this.periods - 1);
		setEndingIndex(output.length - 1);

		for (i = 0; i < this.periods - 1; i++) {
			output[i] = 0;
		}
	}

	/**
	 * @return The number of periods in this indicator.
	 */
	@Override
	public final int getPeriods() {
		return this.periods;
	}
}
