package org.encog.app.analyst.missing;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.script.normalize.AnalystField;

public class MeanAndModeMissing implements HandleMissingValues {

	@Override
	public double[] handleMissing(EncogAnalyst analyst, AnalystField stat) {
		
		// mode?
		if( stat.isClassify() ) {
			int m = stat.determineMode(analyst);
			return stat.encode(m);
		} else {
		// mean
			DataField df = analyst.getScript().findDataField(stat.getName());
			double[] result = new double[1];
			result[0] = df.getMean();
			return result;
		}
	}

}
