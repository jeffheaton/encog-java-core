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
package org.encog.ml.factory.train;

import java.util.Map;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.ml.svm.SVM;
import org.encog.ml.svm.training.SVMTrain;
import org.encog.ml.train.MLTrain;
import org.encog.util.ParamsHolder;

/**
 * A factory to create SVM trainers.
 */
public class SVMFactory {
	
	/**
	 * Create a SVM trainer.
	 * 
	 * @param method
	 *            The method to use.
	 * @param training
	 *            The training data to use.
	 * @param argsStr
	 *            The arguments to use.
	 * @return The newly created trainer.
	 */
	public final MLTrain create(final MLMethod method, 
			final MLDataSet training,
			final String argsStr) {

		if (!(method instanceof SVM)) {
			throw new EncogError(
					"SVM Train training cannot be used on a method of type: "
							+ method.getClass().getName());
		}

		final double defaultGamma = 1.0 / ((SVM) method).getInputCount();
		final double defaultC = 1.0;

		final Map<String, String> args = ArchitectureParse.parseParams(argsStr);
		final ParamsHolder holder = new ParamsHolder(args);
		final double gamma = holder.getDouble(MLTrainFactory.PROPERTY_GAMMA,
				false, defaultGamma);
		final double c = holder.getDouble(MLTrainFactory.PROPERTY_C, false,
				defaultC);

		final SVMTrain result = new SVMTrain((SVM) method, training);
		result.setGamma(gamma);
		result.setC(c);
		return result;
	}
}
