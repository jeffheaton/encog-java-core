package org.encog.ml.data.versatile.normalizers;

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.ColumnDefinition;
import org.encog.ml.data.versatile.Normalizer;

public class IndexedNormalizer implements Normalizer {
	
	@Override
	public int outputSize(ColumnDefinition colDef) {
		return 1;
	}

	@Override
	public int normalizeColumn(ColumnDefinition colDef, String value,
			double[] outputData, int outputColumn) {
		
		if( !colDef.getClasses().contains(value)) {
			throw new EncogError("Undefined value: " + value);
		}
		
		outputData[outputColumn] = colDef.getClasses().indexOf(value);
		return outputColumn+1;
	}

	@Override
	public String denormalizeColumn(ColumnDefinition colDef, MLData data,
			int dataColumn) {
		
		return colDef.getClasses().get((int)data.getData(dataColumn));
	}

}
