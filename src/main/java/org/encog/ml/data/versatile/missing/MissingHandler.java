package org.encog.ml.data.versatile.missing;

import org.encog.ml.data.versatile.ColumnDefinition;
import org.encog.ml.data.versatile.NormalizationHelper;

public interface MissingHandler {

	void init(NormalizationHelper normalizationHelper);

	String process(ColumnDefinition colDef, String value);
	
	double process(ColumnDefinition colDef, double value);

}
