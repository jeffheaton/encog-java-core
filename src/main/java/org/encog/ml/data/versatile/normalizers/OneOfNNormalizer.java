package org.encog.ml.data.versatile.normalizers;

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.ColumnDefinition;
import org.encog.ml.data.versatile.Normalizer;

public class OneOfNNormalizer implements Normalizer {
	
	private double normalizedLow; 
	private double normalizedHigh;
	
	public OneOfNNormalizer(double theNormalizedLow, double theNormalizedHigh) {
		this.normalizedLow = theNormalizedLow;
		this.normalizedHigh = theNormalizedHigh;
	}

	@Override
	public int outputSize(ColumnDefinition colDef) {
		return colDef.getClasses().size();
	}

	@Override
	public int normalizeColumn(ColumnDefinition colDef, String value,
			double[] outputData, int outputColumn) {
		
		for(int i=0;i<colDef.getClasses().size();i++) {
			double d = this.normalizedLow;
			
			if( colDef.getClasses().get(i).equals(value) ) {
				d = this.normalizedHigh;
			}
			
			outputData[outputColumn+i] = d;
		}
		return outputColumn+colDef.getClasses().size();
	}

	@Override
	public String denormalizeColumn(ColumnDefinition colDef, MLData data,
			int dataColumn) {
		double bestValue = Double.NEGATIVE_INFINITY;
		int bestIndex = 0;
		
		for(int i=0;i<data.size();i++) {
			double d = data.getData(dataColumn+i);
			if( d>bestValue) {
				bestValue = d;
				bestIndex = i;
			}
		}
		
		return colDef.getClasses().get(bestIndex);
	}

	@Override
	public int normalizeColumn(ColumnDefinition colDef, double value,
			double[] outputData, int outputColumn) {
		throw new EncogError("Can't use a one-of-n normalizer on a continuous value: " + value);
	}

}
