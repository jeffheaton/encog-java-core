package org.encog.ml.data.versatile;

import org.encog.ml.data.MLData;

public interface NormalizationStrategy {

	int calculateTotalRows(int analyzedRows);

	int normalizedSize(ColumnDefinition colDef, boolean isInput);

	int normalizeColumn(ColumnDefinition colDef, boolean isInput, String value,
			double[] outpuData, int outputColumn);

	String denormalizeColumn(ColumnDefinition colDef, boolean b, MLData output,
			int idx);

}
