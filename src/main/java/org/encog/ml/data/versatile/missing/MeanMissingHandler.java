package org.encog.ml.data.versatile.missing;

import org.encog.EncogError;
import org.encog.ml.data.versatile.ColumnDefinition;
import org.encog.ml.data.versatile.NormalizationHelper;

public class MeanMissingHandler implements MissingHandler {

	@Override
	public void init(NormalizationHelper normalizationHelper) {
		
	}

	@Override
	public String process(ColumnDefinition colDef, String value) {
		throw new EncogError("The mean missing handler only accepts continuous numeric values.");
	}

	@Override
	public double process(ColumnDefinition colDef, double value) {
		return colDef.getMean();
	}

}
