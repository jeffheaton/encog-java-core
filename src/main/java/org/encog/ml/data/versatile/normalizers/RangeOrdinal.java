package org.encog.ml.data.versatile.normalizers;

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.ColumnDefinition;
import org.encog.ml.data.versatile.Normalizer;

public class RangeOrdinal implements Normalizer {

	private double normalizedLow; 
	private double normalizedHigh;
	
	public RangeOrdinal(double theNormalizedLow, double theNormalizedHigh) {
		this.normalizedLow = theNormalizedLow;
		this.normalizedHigh = theNormalizedHigh;
	}
	
	@Override
	public int outputSize(ColumnDefinition colDef) {
		return 1;
	}

	@Override
	public int normalizeColumn(ColumnDefinition colDef, String theValue,
			double[] outputData, int outputColumn) {
		
		// Find the index of the ordinal
		int v = colDef.getClasses().indexOf(theValue);
		if( v==-1 ) {
			throw new EncogError("Unknown ordinal: " + theValue);
		}
		
		double high = colDef.getClasses().size();
		double value = v;
		
		double result = (value/high)
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
	
	@Override
	public int normalizeColumn(ColumnDefinition colDef, double value,
			double[] outputData, int outputColumn) {
		throw new EncogError("Can't ordinal range-normalize a continuous value: " + value);
	}

	@Override
	public String denormalizeColumn(ColumnDefinition colDef, MLData data,
			int dataColumn) {
		
		double high = colDef.getClasses().size();
		double low = 0;
		
		double value = data.getData(dataColumn);
		final double result = ((low - high) * value
				- this.normalizedHigh * low + high
				* this.normalizedLow)
				/ (this.normalizedLow - this.normalizedHigh);
		
		// typically caused by a number that should not have been normalized
		// (i.e. normalization or actual range is infinitely small.
		if( Double.isNaN(result) ) {
			return colDef.getClasses().get(0);
		}
		return colDef.getClasses().get((int)result);
	}
}
