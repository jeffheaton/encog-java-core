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
package org.encog.util.error;

import org.encog.mathutil.error.ErrorCalculation;
import org.encog.ml.MLContext;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;

public class CalculateRegressionError {
	
	public static double calculateError(final MLRegression method, 
			final MLDataSet data) {
		final ErrorCalculation errorCalculation = new ErrorCalculation();
		
		// clear context
		if( method instanceof MLContext ) {
			((MLContext)method).clearContext();
		}

		// calculate error
		for (final MLDataPair pair : data) {
			final MLData actual = method.compute(pair.getInput());
			errorCalculation.updateError(actual.getData(), pair.getIdeal()
					.getData(),pair.getSignificance());
		}
		return errorCalculation.calculate();
	}
}
