package org.encog.ml.data.versatile;

import org.encog.ml.data.MLData;

public interface Normalizer {

	int outputSize(ColumnDefinition colDef);

	int normalizeColumn(ColumnDefinition colDef, String value,
			double[] outputData, int outputColumn);

	String denormalizeColumn(ColumnDefinition colDef, MLData data,
			int dataColumn);

}
