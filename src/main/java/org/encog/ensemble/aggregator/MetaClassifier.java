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

package org.encog.ensemble.aggregator;

import java.util.ArrayList;

import org.encog.ensemble.EnsembleAggregator;
import org.encog.ensemble.EnsembleML;
import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.GenericEnsembleML;
import org.encog.ensemble.data.EnsembleDataSet;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

public class MetaClassifier implements EnsembleAggregator {

	EnsembleML classifier;
	EnsembleMLMethodFactory mlFact;
	EnsembleTrainFactory etFact;
	double trainError;
	int members;
	boolean adaptiveError = false;

	public MetaClassifier(double trainError, EnsembleMLMethodFactory mlFact, EnsembleTrainFactory etFact, boolean adaptiveError)
	{
		this.trainError = trainError;
		this.mlFact = mlFact;
		this.etFact = etFact;
		this.adaptiveError = adaptiveError;
		members = 1;
	}

	public MetaClassifier(double trainError, EnsembleMLMethodFactory mlFact, EnsembleTrainFactory etFact)
	{
		this(trainError, mlFact, etFact, false);
	}

	public double getTrainingError()
	{
		return trainError;
	}
	
	public void setTrainingError(double trainError)
	{
		this.trainError = trainError;
	}

	@Override
	public void setNumberOfMembers(int members)
	{
		this.members = members;
	}
	
	@Override
	public MLData evaluate(ArrayList<MLData> outputs) {
		BasicMLData merged_outputs = new BasicMLData(classifier.getInputCount());
		for(MLData output:outputs) {
			int index = 0;
			for(double val:output.getData()) {
				merged_outputs.add(index++,val);
			}
		}
		return classifier.compute(merged_outputs);
	}

	@Override
	public String getLabel()
	{
		String ret = "metaclassifier-" + mlFact.getLabel() + "-" + trainError + "-" + etFact.getLabel();
		if(adaptiveError)
		{
			ret += "-adaptive";
		}
		return ret;
	}


	@Override
	public void train()
	{
		if (classifier != null)
		{
			double targetError = adaptiveError ? trainError / members : trainError; 
			classifier.train(targetError);
		}
		else
		{
			System.err.println("Trying to train a null classifier in MetaClassifier");
		}
	}

	@Override
	public void setTrainingSet(EnsembleDataSet trainingSet)
	{
		mlFact.setSizeMultiplier(members);
		classifier = new GenericEnsembleML(mlFact.createML(trainingSet.getInputSize(), trainingSet.getIdealSize()),mlFact.getLabel());
		classifier.setTraining(etFact.getTraining(classifier.getMl(), trainingSet));
		classifier.setTrainingSet(trainingSet);
	}

	@Override
	public boolean needsTraining()
	{
		return true;
	}
}
