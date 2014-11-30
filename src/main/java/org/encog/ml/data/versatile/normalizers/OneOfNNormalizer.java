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
package org.encog.ml.data.versatile.normalizers;

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.columns.ColumnDefinition;

/**
 * Normalize to one-of-n for nominal values. For example, "one", "two", "three"
 * becomes 1,0,0 and 0,1,0 and 0,0,1 etc. Assuming 0 and 1 were the min/max.
 */
public class OneOfNNormalizer implements Normalizer {
	private static final long serialVersionUID = 1L;

	/**
	 * The normalized low.
	 */
	private double normalizedLow;
	
	/**
	 * The normalized high.
	 */
	private double normalizedHigh;

	/**
	 * Construct the normalizer.
	 * @param theNormalizedLow The normalized low.
	 * @param theNormalizedHigh The normalized high.
	 */
	public OneOfNNormalizer(double theNormalizedLow, double theNormalizedHigh) {
		this.normalizedLow = theNormalizedLow;
		this.normalizedHigh = theNormalizedHigh;
	}

 	@Override
 	public boolean equals(Object obj) { 
 		boolean result;
 		
 		if ( obj instanceof OneOfNNormalizer ) {
 			OneOfNNormalizer that = (OneOfNNormalizer) obj;
 			result = Double.valueOf( this.normalizedHigh ).equals( that.normalizedHigh )
 					&& Double.valueOf( this.normalizedLow ).equals( that.normalizedLow );
 		} else {
 			result = false;
 		}
 		
 		return result;
 	}
 	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int outputSize(ColumnDefinition colDef) {
		return colDef.getClasses().size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int normalizeColumn(ColumnDefinition colDef, String value,
			double[] outputData, int outputColumn) {

		for (int i = 0; i < colDef.getClasses().size(); i++) {
			double d = this.normalizedLow;

			if (colDef.getClasses().get(i).equals(value)) {
				d = this.normalizedHigh;
			}

			outputData[outputColumn + i] = d;
		}
		return outputColumn + colDef.getClasses().size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String denormalizeColumn(ColumnDefinition colDef, MLData data,
			int dataColumn) {
		double bestValue = Double.NEGATIVE_INFINITY;
		int bestIndex = 0;

		for (int i = 0; i < data.size(); i++) {
			double d = data.getData(dataColumn + i);
			if (d > bestValue) {
				bestValue = d;
				bestIndex = i;
			}
		}

		return colDef.getClasses().get(bestIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int normalizeColumn(ColumnDefinition colDef, double value,
			double[] outputData, int outputColumn) {
		throw new EncogError(
				"Can't use a one-of-n normalizer on a continuous value: "
						+ value);
	}

}
