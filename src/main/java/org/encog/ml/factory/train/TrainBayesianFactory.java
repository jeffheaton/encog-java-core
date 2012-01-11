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

import org.encog.ml.MLMethod;
import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.training.BayesianInit;
import org.encog.ml.bayesian.training.TrainBayesian;
import org.encog.ml.bayesian.training.estimator.BayesEstimator;
import org.encog.ml.bayesian.training.estimator.EstimatorNone;
import org.encog.ml.bayesian.training.estimator.SimpleEstimator;
import org.encog.ml.bayesian.training.search.SearchNone;
import org.encog.ml.bayesian.training.search.k2.BayesSearch;
import org.encog.ml.bayesian.training.search.k2.SearchK2;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.ml.train.MLTrain;
import org.encog.util.ParamsHolder;

public class TrainBayesianFactory {
	/**
	 * Create a K2 trainer.
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
			final MLDataSet training, final String argsStr) {
		final Map<String, String> args = ArchitectureParse.parseParams(argsStr);
		final ParamsHolder holder = new ParamsHolder(args);

		final int maxParents = holder.getInt(
				MLTrainFactory.PROPERTY_MAX_PARENTS, false, 1);
		String searchStr = holder.getString("SEARCH", false, "k2");
		String estimatorStr = holder.getString("ESTIMATOR", false, "simple");
		String initStr = holder.getString("INIT", false, "naive");
		
		BayesSearch search;
		BayesEstimator estimator;
		BayesianInit init;
		
		if( searchStr.equalsIgnoreCase("k2")) {
			search = new SearchK2();
		} else if( searchStr.equalsIgnoreCase("none")) {
			search = new SearchNone();
		}
		else {
			throw new BayesianError("Invalid search type: " + searchStr);
		}
		
		if( estimatorStr.equalsIgnoreCase("simple")) {
			estimator = new SimpleEstimator();
		} else if( estimatorStr.equalsIgnoreCase("none")) {
			estimator = new EstimatorNone();
		}
		else {
			throw new BayesianError("Invalid estimator type: " + estimatorStr);
		}
		
		if( initStr.equalsIgnoreCase("simple")) {
			init = BayesianInit.InitEmpty;
		} else if( initStr.equalsIgnoreCase("naive")) {
			init = BayesianInit.InitNaiveBayes;
		} else if( initStr.equalsIgnoreCase("none")) {
			init = BayesianInit.InitNoChange;
		}
		else {
			throw new BayesianError("Invalid init type: " + initStr);
		}
		
		return new TrainBayesian((BayesianNetwork) method, training, maxParents, init, search, estimator);
	}
}
