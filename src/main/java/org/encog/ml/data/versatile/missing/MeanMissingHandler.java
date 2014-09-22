package org.encog.ml.data.versatile.missing;

import org.encog.EncogError;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.columns.ColumnDefinition;

/**
 * Handle missing data by using the mean value of that column.
 */
public class MeanMissingHandler implements MissingHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(NormalizationHelper normalizationHelper) {
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String processString(ColumnDefinition colDef) {
		throw new EncogError("The mean missing handler only accepts continuous numeric values.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double processDouble(ColumnDefinition colDef) {
		return colDef.getMean();
	}

}
