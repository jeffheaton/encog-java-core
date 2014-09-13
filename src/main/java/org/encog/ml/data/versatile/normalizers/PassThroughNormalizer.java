package org.encog.ml.data.versatile.normalizers;

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
		outputData[outputColumn]=Double.parseDouble(value);
		return outputColumn+1;
	}

	@Override
	public String denormalizeColumn(ColumnDefinition colDef, MLData data,
			int dataColumn) {
		return ""+data.getData(dataColumn);
	}

}
