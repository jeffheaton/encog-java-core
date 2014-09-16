package org.encog.ml.data.versatile.normalizers;

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.ColumnDefinition;
import org.encog.ml.data.versatile.Normalizer;

public class RangeNormalizer implements Normalizer {

	private double normalizedLow; 
	private double normalizedHigh;
	
	public RangeNormalizer(double theNormalizedLow, double theNormalizedHigh) {
		this.normalizedLow = theNormalizedLow;
		this.normalizedHigh = theNormalizedHigh;
	}
	
	@Override
	public int outputSize(ColumnDefinition colDef) {
		return 1;
	}

	@Override
	public int normalizeColumn(ColumnDefinition colDef, String value,
			double[] outputData, int outputColumn) {
		throw new EncogError("Can't range-normalize a string value: " + value);

	}
	
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
