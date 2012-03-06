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
package org.encog.util.validate;

import org.encog.EncogError;
import org.encog.ml.MLInput;
import org.encog.ml.MLMethod;
import org.encog.ml.MLOutput;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.pnn.BasicPNN;

public class ValidateNetwork {
	
	
	public static void validateMethodToData(MLMethod method, MLDataSet training) {
		
		if( !(method instanceof MLInput) || !(method instanceof MLOutput) ) {
			throw new EncogError("This machine learning method is not compatible with the provided data.");
		}
		
		int trainingInputCount = training.getInputSize();
		int trainingOutputCount = training.getIdealSize();
		int methodInputCount = 0;
		int methodOutputCount = 0;
		
		if( method instanceof MLInput ) {
			methodInputCount = ((MLInput)method).getInputCount();
		}
		
		if( method instanceof MLOutput ) {
			methodOutputCount = ((MLOutput)method).getOutputCount();
		}
		
		if( methodInputCount != trainingInputCount ) {
			throw new EncogError("The machine learning method has an input length of " + methodInputCount + ", but the training data has " + trainingInputCount  + ". They must be the same.");
		}
		
		if (!(method instanceof BasicPNN)) {
			if (trainingOutputCount > 0
					&& methodOutputCount != trainingOutputCount) {
				throw new EncogError(
						"The machine learning method has an output length of "
								+ methodOutputCount
								+ ", but the training data has "
								+ trainingOutputCount
								+ ". They must be the same.");
			}
		}
		
	}
	
}
