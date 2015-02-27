/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.ensemble.data.factories;

import java.util.Random;

import org.encog.ensemble.data.EnsembleDataSet;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;

public class WeightedResamplingDataSetFactory extends EnsembleDataSetFactory {

	public WeightedResamplingDataSetFactory(int dataSetSize) {
		super(dataSetSize);
	}

	MLDataSet originalData;

	MLDataPair getCandidate(double weight) {
		double weightSoFar = 0;
		for (int i = 0; i < dataSource.size(); i++) {
			weightSoFar += dataSource.get(i).getSignificance();
			if (weightSoFar > weight)
				return (MLDataPair) dataSource.get(i);
		}
		return (MLDataPair) dataSource.get(dataSource.size());
	}

	@Override
	public EnsembleDataSet getNewDataSet() {
		double weightSum = 0;
		for (int i = 0; i < dataSource.size(); i++)
			weightSum += dataSource.get(i).getSignificance();
		Random generator = new Random();
		EnsembleDataSet ds = new EnsembleDataSet(dataSource.getInputSize(), dataSource.getIdealSize());
		for (int i = 0; i < dataSetSize; i++)
		{
			double candidate = generator.nextDouble() * weightSum;
			ds.add(getCandidate(candidate));
		}
		return ds;
	}

}
