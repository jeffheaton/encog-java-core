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
import org.encog.NullStatusReportable;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.ml.svm.SVM;
import org.encog.ml.svm.training.SVMSearchTrain;
import org.encog.ml.train.MLTrain;
import org.encog.util.ParamsHolder;

/**
 * A factory that creates SVM-search trainers.
 */
public class SVMSearchFactory {
	
	/**
	 * Property for gamma.
	 */
	public static final String PROPERTY_GAMMA1 = "GAMMA1";
	
	/**
	 * Property for constant.
	 */
	public static final String PROPERTY_C1 = "C1";
	
	/**
	 * Property for gamma.
	 */
	public static final String PROPERTY_GAMMA2 = "GAMMA2";
	
	/**
	 * Property for constant.
	 */
	public static final String PROPERTY_C2 = "C2";
	
	/**
	 * Property for gamma.
	 */
	public static final String PROPERTY_GAMMA_STEP = "GAMMASTEP";
	
	/**
	 * Property for constant.
	 */
	public static final String PROPERTY_C_STEP = "CSTEP";

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
		
		final Map<String, String> args = ArchitectureParse.parseParams(argsStr);
		new ParamsHolder(args);

		final ParamsHolder holder = new ParamsHolder(args);
		final double gammaStart = holder.getDouble(SVMSearchFactory.PROPERTY_GAMMA1, false, SVMSearchTrain.DEFAULT_GAMMA_BEGIN);
		final double cStart = holder.getDouble(SVMSearchFactory.PROPERTY_C1, false, SVMSearchTrain.DEFAULT_CONST_BEGIN);
		final double gammaStop = holder.getDouble(SVMSearchFactory.PROPERTY_GAMMA2, false, SVMSearchTrain.DEFAULT_GAMMA_END);
		final double cStop = holder.getDouble(SVMSearchFactory.PROPERTY_C2, false, SVMSearchTrain.DEFAULT_CONST_END);
		final double gammaStep = holder.getDouble(SVMSearchFactory.PROPERTY_GAMMA_STEP, false, SVMSearchTrain.DEFAULT_GAMMA_STEP);
		final double cStep = holder.getDouble(SVMSearchFactory.PROPERTY_C_STEP, false, SVMSearchTrain.DEFAULT_CONST_STEP);
		
		final SVMSearchTrain result = new SVMSearchTrain((SVM)method, training);
		
		result.setGammaBegin(gammaStart);
		result.setGammaEnd(gammaStop);
		result.setGammaStep(gammaStep);
		result.setConstBegin(cStart);
		result.setConstEnd(cStop);
		result.setConstStep(cStep);

		return result;
	}
}
