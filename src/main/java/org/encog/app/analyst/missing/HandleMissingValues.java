package org.encog.app.analyst.missing;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.normalize.AnalystField;

public interface HandleMissingValues {	
	double []handleMissing(EncogAnalyst analyst, AnalystField stat);
}
