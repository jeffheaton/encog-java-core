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
 * A a range normalizer forces a value to fall in a specific range.
 *
 */
public class RangeNormalizer implements Normalizer {
	private static final long serialVersionUID = 1L;

	/**
	 * The normalized low value.
	 */
	private double normalizedLow;
	
	/**
	 * The normalized high value.
	 */
	private double normalizedHigh;
	
	/**
	 * Construct the range normalizer.
	 * @param theNormalizedLow The normalized low value.
	 * @param theNormalizedHigh The normalized high value.
	 */
	public RangeNormalizer(double theNormalizedLow, double theNormalizedHigh) {
		this.normalizedLow = theNormalizedLow;
		this.normalizedHigh = theNormalizedHigh;
	}
	

 	@Override
 	public boolean equals(Object obj) { 
 		boolean result;
 		
 		if ( obj instanceof RangeNormalizer ) {
 			RangeNormalizer that = (RangeNormalizer) obj;
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
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int normalizeColumn(ColumnDefinition colDef, String value,
			double[] outputData, int outputColumn) {
		throw new EncogError("Can't range-normalize a string value: " + value);

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int normalizeColumn(ColumnDefinition colDef, double value,
			double[] outputData, int outputColumn) {
		double result = ((value - colDef.getLow()) / (colDef.getHigh() - colDef.getLow()))
				* (this.normalizedHigh - this.normalizedLow)
				+ this.normalizedLow;
		
		// typically caused by a number that should not have been normalized
		// (i.e. normalization or actual range is infinitely small.
		if( Double.isNaN(result) ) {
			result = ((this.normalizedHigh-this.normalizedLow)/2)+this.normalizedLow;
		} 
		
		outputData[outputColumn] = result;
	
		return outputColumn+1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String denormalizeColumn(ColumnDefinition colDef, MLData data,
			int dataColumn) {
		
		double value = data.getData(dataColumn);
		final double result = ((colDef.getLow() - colDef.getHigh()) * value
				- this.normalizedHigh * colDef.getLow() + colDef.getHigh()
				* this.normalizedLow)
				/ (this.normalizedLow - this.normalizedHigh);
		
		// typically caused by a number that should not have been normalized
		// (i.e. normalization or actual range is infinitely small.
		if( Double.isNaN(result) ) {
			return ""+(((this.normalizedHigh-this.normalizedLow)/2)+this.normalizedLow);
		}
		return ""+result;
	}
}
