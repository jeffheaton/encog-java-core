package org.encog.ml.data.versatile.normalizers.strategies;

import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.columns.ColumnDefinition;

/**
 * Defines the interface to a normalization strategy.
 */
public interface NormalizationStrategy {

	/**
	 * Calculate how many elements a column will normalize into.
	 * @param colDef The column definition.
	 * @param isInput True, if this is an input column.
	 * @return The number of elements needed to normalize this column.
	 */
	int normalizedSize(ColumnDefinition colDef, boolean isInput);

	/**
	 * Normalize a column, with a string input.
	 * @param colDef The column definition.
	 * @param isInput True, if this is an input column.
	 * @param value The value to normalize.
	 * @param outpuData The output data.
	 * @param outputColumn The element to begin outputing to.
	 * @return The new output element, advanced by the correct amount.
	 */
	int normalizeColumn(ColumnDefinition colDef, boolean isInput, String value,
			double[] outpuData, int outputColumn);

	/**
	 * Normalize a column, with a double input.
	 * @param colDef The column definition.
	 * @param isInput True, if this is an input column.
	 * @param value The value to normalize.
	 * @param outpuData The output data.
	 * @param outputColumn The element to begin outputing to.
	 * @return The new output element, advanced by the correct amount.
	 */
	String denormalizeColumn(ColumnDefinition colDef, boolean isInput, MLData output,
			int idx);

	/**
	 * Normalize a column, with a double value.
	 * @param colDef The column definition.
	 * @param isInput True, if this is an input column.
	 * @param value The value to normalize.
	 * @param outpuData The output data.
	 * @param outputColumn The element to begin outputing to.
	 * @return The new output element, advanced by the correct amount.
	 */
	int normalizeColumn(ColumnDefinition colDef, boolean isInput, double value,
			double[] outpuData, int outputColumn);

}
