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
package org.encog.ml.bayesian.training.search;

import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.training.TrainBayesian;
import org.encog.ml.bayesian.training.search.k2.BayesSearch;
import org.encog.ml.data.MLDataSet;

/**
 * Simple class to perform no search for optimal network structure.
 */
public class SearchNone implements BayesSearch {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(TrainBayesian theTrainer, BayesianNetwork theNetwork,
			MLDataSet theData) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean iteration() {
		return false;
		
	}

}
