package org.encog.ml.data.versatile.missing;

import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.columns.ColumnDefinition;

/**
 * Specifies how to handle missing data.
 */
public interface MissingHandler {

	/**
	 * Called by the normalizer to setup this handler.
	 * @param normalizationHelper The normalizer that is being used.
	 */
	void init(NormalizationHelper normalizationHelper);

	/**
	 * Process a column's missing data.
	 * @param colDef The column that is missing.
	 * @return The value to use.
	 */
	String processString(ColumnDefinition colDef);
	
	double processDouble(ColumnDefinition colDef);

}
