package org.encog.ml.data.versatile.normalizers;

import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.columns.ColumnDefinition;

/**
 * The normalizer interface defines how to normalize a column.  The source of the
 * normalization can be either string or double.
 */
public interface Normalizer {

	/**
	 * Determine the normalized size of the specified column.
	 * @param colDef The column to check.
	 * @return The size of the column normalized.
	 */
	int outputSize(ColumnDefinition colDef);

	/**
	 * Normalize a column from a string. The output will go to an array, starting at outputColumn.
	 * @param colDef The column that is being normalized.
	 * @param value The value to normalize.
	 * @param outputData The array to output to.
	 * @param outputIndex The index to start at in outputData.
	 * @return The new index (in outputData) that we've moved to.
	 */
	int normalizeColumn(ColumnDefinition colDef, String value,
			double[] outputData, int outputIndex);

	/**
	 * Normalize a column from a double. The output will go to an array, starting at outputColumn.
	 * @param colDef The column that is being normalized.
	 * @param value The value to normalize.
	 * @param outputData The array to output to.
	 * @param outputIndex The index to start at in outputData.
	 * @return The new index (in outputData) that we've moved to.
	 */
	int normalizeColumn(ColumnDefinition colDef, double value,
			double[] outputData, int outputIndex);
	
	/**
	 * Denormalize a value.
	 * @param colDef The column to denormalize.
	 * @param data The data to denormalize.
	 * @param dataIndex The starting location inside data.
	 * @return The denormalized value.
	 */
	String denormalizeColumn(ColumnDefinition colDef, MLData data,
			int dataIndex);
}
