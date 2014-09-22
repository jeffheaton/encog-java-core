package org.encog.ml.data.versatile.normalizers;

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.columns.ColumnDefinition;

/**
 * Normalize to one-of-n for nominal values. For example, "one", "two", "three"
 * becomes 1,0,0 and 0,1,0 and 0,0,1 etc. Assuming 0 and 1 were the min/max.
 */
public class OneOfNNormalizer implements Normalizer {

	/**
	 * The normalized low.
	 */
	private double normalizedLow;
	
	/**
	 * The normalized high.
	 */
	private double normalizedHigh;

	/**
	 * Construct the normalizer.
	 * @param theNormalizedLow The normalized low.
	 * @param theNormalizedHigh The normalized high.
	 */
	public OneOfNNormalizer(double theNormalizedLow, double theNormalizedHigh) {
		this.normalizedLow = theNormalizedLow;
		this.normalizedHigh = theNormalizedHigh;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int outputSize(ColumnDefinition colDef) {
		return colDef.getClasses().size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int normalizeColumn(ColumnDefinition colDef, String value,
			double[] outputData, int outputColumn) {

		for (int i = 0; i < colDef.getClasses().size(); i++) {
			double d = this.normalizedLow;

			if (colDef.getClasses().get(i).equals(value)) {
				d = this.normalizedHigh;
			}

			outputData[outputColumn + i] = d;
		}
		return outputColumn + colDef.getClasses().size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String denormalizeColumn(ColumnDefinition colDef, MLData data,
			int dataColumn) {
		double bestValue = Double.NEGATIVE_INFINITY;
		int bestIndex = 0;

		for (int i = 0; i < data.size(); i++) {
			double d = data.getData(dataColumn + i);
			if (d > bestValue) {
				bestValue = d;
				bestIndex = i;
			}
		}

		return colDef.getClasses().get(bestIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int normalizeColumn(ColumnDefinition colDef, double value,
			double[] outputData, int outputColumn) {
		throw new EncogError(
				"Can't use a one-of-n normalizer on a continuous value: "
						+ value);
	}

}
