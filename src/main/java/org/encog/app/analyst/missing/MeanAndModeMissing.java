/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
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
