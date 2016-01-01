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
import org.encog.ml.MLClassification;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.train.MLTrain;

/**
 * @author nitbix
 *
 */
public interface EnsembleML extends MLMethod, MLClassification, MLRegression {

	/**
	 * Set the dataset for this member
	 * @param dataSet The data set.
	 */
	public void setTrainingSet(EnsembleDataSet dataSet);

	/**
	 * Set the training for this member
	 * @param train The trainer.
	 */
	public void setTraining(MLTrain train);

	/**
	 * @return Get the dataset for this member
	 */
	public EnsembleDataSet getTrainingSet();

	/**
	 * @return Get the dataset for this member.
	 */
	public MLTrain getTraining();

	/**
	 * Train the ML to a certain accuracy.
	 * @param targetError The target error.
	 */
	public void train(double targetError);

	/**
	 * Train the ML to a certain accuracy.
	 * @param targetError Target error.
	 * @param verbose Verbose mode.
	 * @param maxIterations Stop after this number of iterations
	 */
	public void train(double targetError, int maxIterations, boolean verbose);


	/**
	 * Train the ML to a certain accuracy.
	 * @param targetError Target error.
	 * @param maxIterations Stop after this number of iterations
	 */
	public void train(double targetError, int maxIterations);

	/**
	 * Train the ML to a certain accuracy.
	 * @param targetError Target error.
	 * @param verbose Verbose mode.
	 */
	public void train(double targetError, boolean verbose);

	/**
	 * Get the error for this ML on the dataset
	 * @param testset The dataset.
	 */
	public double getError(EnsembleDataSet testset);

	/**
	 * Set the MLMethod to run
	 * @param newMl The new ML.
	 */
	public void setMl(MLMethod newMl);

	/**
	 * @return Returns the current MLMethod
	 */
	public MLMethod getMl();

	public void trainStep();

	public String getLabel();
}