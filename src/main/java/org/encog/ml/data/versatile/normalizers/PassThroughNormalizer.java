package org.encog.ml.data.versatile.normalizers;

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.ColumnDefinition;
import org.encog.ml.data.versatile.Normalizer;

public class PassThroughNormalizer implements Normalizer {

	@Override
	public int outputSize(ColumnDefinition colDef) {
		return 1;
	}

	@Override
	public int normalizeColumn(ColumnDefinition colDef, String value,
			double[] outputData, int outputColumn) {
		throw new EncogError("Can't use a pass-through normalizer on a string value: "+value);
	}

	@Override
	public String denormalizeColumn(ColumnDefinition colDef, MLData data,
			int dataColumn) {
		return ""+data.getData(dataColumn);
	}

	@Override
	public int normalizeColumn(ColumnDefinition colDef, double value,
			double[] outputData, int outputColumn) {
		outputData[outputColumn]=value;
		return outputColumn+1;
	}

}
