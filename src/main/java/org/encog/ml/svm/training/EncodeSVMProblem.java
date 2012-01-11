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
package org.encog.ml.svm.training;

import org.encog.EncogError;
import org.encog.mathutil.libsvm.svm_node;
import org.encog.mathutil.libsvm.svm_problem;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;

/**
 * Encode an Encog dataset as a SVM problem.
 */
public final class EncodeSVMProblem {

	/**
	 * Encode the Encog dataset.
	 * 
	 * @param training
	 *            The training data.
	 * @param outputIndex
	 *            The ideal element to use, this is necessary because SVM's have
	 *            only a single output. This value is typically zero.
	 * @return The SVM problem.
	 */
	public static svm_problem encode(final MLDataSet training,
			final int outputIndex) {
		try {
			final svm_problem result = new svm_problem();

			result.l = (int) training.getRecordCount();

			result.y = new double[result.l];
			result.x = new svm_node[result.l][training.getInputSize()];

			int elementIndex = 0;

			for (final MLDataPair pair : training) {
				final MLData input = pair.getInput();
				final MLData output = pair.getIdeal();
				result.x[elementIndex] = new svm_node[input.size()];

				for (int i = 0; i < input.size(); i++) {
					result.x[elementIndex][i] = new svm_node();
					result.x[elementIndex][i].index = i + 1;
					result.x[elementIndex][i].value = input.getData(i);
				}

				result.y[elementIndex] = output.getData(outputIndex);

				elementIndex++;
			}

			return result;
		} catch (final OutOfMemoryError e) {
			throw new EncogError("SVM Model - Out of Memory");
		}
	}

	/**
	 * Private constructor.
	 */
	private EncodeSVMProblem() {

	}

}
