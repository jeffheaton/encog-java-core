package org.encog.ml.data.versatile.normalizers;

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.columns.ColumnDefinition;

/**
 * Normalize ordinal/nominal values to a single value that is simply the index
 * of the class in the list. For example, "one", "two", "three" normalizes to
 * 0,1,2.
 */
public class IndexedNormalizer implements Normalizer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int outputSize(ColumnDefinition colDef) {
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int normalizeColumn(ColumnDefinition colDef, String value,
			double[] outputData, int outputColumn) {

		if (!colDef.getClasses().contains(value)) {
			throw new EncogError("Undefined value: " + value);
		}

		outputData[outputColumn] = colDef.getClasses().indexOf(value);
		return outputColumn + 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String denormalizeColumn(ColumnDefinition colDef, MLData data,
			int dataColumn) {

		return colDef.getClasses().get((int) data.getData(dataColumn));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int normalizeColumn(ColumnDefinition colDef, double value,
			double[] outputData, int outputColumn) {
		throw new EncogError(
				"Can't use an indexed normalizer on a continuous value: "
						+ value);
	}

}
