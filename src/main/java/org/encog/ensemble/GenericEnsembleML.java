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
package org.encog.ensemble;

import org.encog.ensemble.data.EnsembleDataSet;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.EngineArray;

public class GenericEnsembleML implements EnsembleML {

	private EnsembleDataSet trainingSet;
	private BasicNetwork ml;
	private MLTrain trainer;
	private String label;

	public GenericEnsembleML(MLMethod fromML, String description) {
		setMl(fromML);
		this.label = description;
	}

	@Override
	public void setTrainingSet(EnsembleDataSet dataSet) {
		this.trainingSet = dataSet;
	}

	@Override
	public EnsembleDataSet getTrainingSet() {
		return trainingSet;
	}

	@Override
	public void train(double targetError, boolean verbose) {
		double error = 0;
		double previouserror = 0;
		double errordelta = 0;
		int iteration = 0;
		do {
			trainer.iteration();
			iteration++;
			if (iteration > 1) {
				previouserror = error;
			}
			error = trainer.getError();
			if (iteration > 1) {
				errordelta = previouserror - error;
			}
			if (verbose) System.out.println(iteration + " " + error);
		} while ((error > targetError) &&
				 trainer.canContinue() &&
				 errordelta > -0.1 &&
				 //make this a parameter
				 iteration < 2000);
		trainer.finishTraining();
	}

	@Override
	public void setMl(MLMethod newMl) {
		ml = (BasicNetwork) newMl;
	}

	@Override
	public MLMethod getMl() {
		return ml;
	}

	@Override
	public int classify(MLData input) {
		return ml.classify(input);
	}

	@Override
	public MLData compute(MLData input) {
		return ml.compute(input);
	}

	@Override
	public int getInputCount() {
		return ml.getInputCount();
	}

	@Override
	public int getOutputCount() {
		return ml.getOutputCount();
	}

	@Override
	public void train(double targetError) {
		train(targetError, false);
	}

	public int winner(MLData output) {
		return EngineArray.maxIndex(output.getData());
	}

	@Override
	public void setTraining(MLTrain train) {
		trainer = train;
	}

	@Override
	public MLTrain getTraining() {
		return trainer;
	}

	@Override
	public void trainStep() {
		trainer.iteration();
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public double getError(EnsembleDataSet testset) {
		return ml.calculateError(testset);
	}
}
