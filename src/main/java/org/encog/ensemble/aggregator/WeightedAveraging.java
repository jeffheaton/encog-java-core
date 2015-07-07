/*
 * Encog(tm) Core v3.2 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2013 Heaton Research, Inc.
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
package org.encog.ensemble.aggregator;

import java.util.ArrayList;
import java.util.List;

import org.encog.ensemble.EnsembleWeightedAggregator;
import org.encog.ensemble.data.EnsembleDataSet;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

public class WeightedAveraging implements EnsembleWeightedAggregator {

	private ArrayList<Double> weights;
	
	public class WeightMismatchException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 327652547599703252L;
		
	}
	
	public WeightedAveraging(List<Double> weights)
	{
		this.weights = (ArrayList<Double>) weights;
	}
	
	@Override
	public void setWeights(List<Double> weights)
	{
		this.weights = (ArrayList<Double>) weights;
	}

	@Override
	public List<Double> getWeights()
	{
		return this.weights;
	}	
	
	@Override
	public MLData evaluate(ArrayList<MLData> outputs) throws WeightMismatchException {
		int outputSize = outputs.get(0).size();
		double weightSum = 0;
		if (weights == null || weights.size() != outputs.size())
		{
			throw new WeightMismatchException();
		}
		BasicMLData acc = new BasicMLData(outputSize);
		for (int i = 0; i < outputs.size(); i++)
		{
			BasicMLData out = (BasicMLData) outputs.get(i);
			out = (BasicMLData) out.times(weights.get(i));
			acc = (BasicMLData) acc.plus(out);
			weightSum += weights.get(i);
		}

		if(weightSum == 0)
		{
			weightSum = 1;
		}
		acc = (BasicMLData) acc.times(1.0 / weightSum);
		return 	acc;

	}

	@Override
	public String getLabel() {
		return "weightedaveraging";
	}

	@Override
	public void train() {
		//This is a no-op in this aggregator
	}

	@Override
	public void setTrainingSet(EnsembleDataSet trainingSet) {
		// This is a no-op in this aggregator.
	}

	@Override
	public boolean needsTraining() {
		return false;
	}

	@Override
	public void setNumberOfMembers(int members) {
		//does nothing
	}
}
