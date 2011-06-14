package org.encog.app.analyst.missing;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.normalize.AnalystField;

public class NegateMissing implements HandleMissingValues {

	@Override
	public double[] handleMissing(EncogAnalyst analyst, AnalystField stat) {
		double[] result = new double[stat.getColumnsNeeded()];
		double n = stat.getNormalizedHigh() - (stat.getNormalizedHigh()-stat.getNormalizedLow()/2);
		for(int i=0;i<result.length;i++) {
			result[i] = n;
		}
		return result;
	}
	
}
