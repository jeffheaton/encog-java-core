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
package org.encog.app.analyst.commands;

import java.io.File;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.ml.MLMethod;
import org.encog.ml.MLResettable;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.logging.EncogLogging;
import org.encog.util.simple.EncogUtility;
import org.encog.util.validate.ValidateNetwork;

/**
 * This command is used to perform training on a machine learning method and
 * dataset.
 * 
 */
public class CmdTrain extends Cmd {

	/**
	 * The name of this command.
	 */
	public static final String COMMAND_NAME = "TRAIN";
	
	/**
	 * The number of folds, if kfold is used.
	 */
	private int kfold;

	/**
	 * Construct the train command.
	 * @param analyst The analyst to use.
	 */
	public CmdTrain(final EncogAnalyst analyst) {
		super(analyst);
	}

	/**
	 * Create a trainer, use cross validation if enabled.
	 * @param method The method to use.
	 * @param trainingSet The training set to use.
	 * @return The trainer.
	 */
	private MLTrain createTrainer(final MLMethod method,
			final MLDataSet trainingSet) {

		final MLTrainFactory factory = new MLTrainFactory();

		final String type = getProp().getPropertyString(
				ScriptProperties.ML_TRAIN_TYPE);
		final String args = getProp().getPropertyString(
				ScriptProperties.ML_TRAIN_ARGUMENTS);
		
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "training type:" + type);
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "training args:" + args);
		
		if( method instanceof MLResettable ) {
			this.getAnalyst().setMethod(method);
		}


		MLTrain train = factory.create(method, trainingSet, type, args);

		if (this.kfold > 0) {
			train = new CrossValidationKFold(train, this.kfold);
		}

		return train;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean executeCommand(final String args) {

		this.kfold = obtainCross();
		final MLDataSet trainingSet = obtainTrainingSet();
		MLMethod method = obtainMethod();
		final MLTrain trainer = createTrainer(method, trainingSet);
		
		if( method instanceof BayesianNetwork ) {
			final String query = getProp().getPropertyString(
					ScriptProperties.ML_CONFIG_QUERY);
			((BayesianNetwork)method).defineClassificationStructure(query);
		}
		
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Beginning training");

		performTraining(trainer, method, trainingSet);

		final String resourceID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_MACHINE_LEARNING_FILE);
		final File resourceFile = getAnalyst().getScript().resolveFilename(
				resourceID);
		method = trainer.getMethod();
		EncogDirectoryPersistence.saveObject(resourceFile, method);
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "save to:" + resourceID);
		trainingSet.close();

		return getAnalyst().shouldStopCommand();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getName() {
		return CmdTrain.COMMAND_NAME;
	}

	/**
	 * Obtain the number of folds for cross validation.
	 * @return The number of folds.
	 */
	private int obtainCross() {
		final String cross = getProp().getPropertyString(
				ScriptProperties.ML_TRAIN_CROSS);
		if ((cross == null) || (cross.length() == 0)) {
			return 0;
		} else if (cross.toLowerCase().startsWith("kfold:")) {
			final String str = cross.substring(6);
			try {
				return Integer.parseInt(str);
			} catch (final NumberFormatException ex) {
				throw new AnalystError("Invalid kfold :" + str);
			}
		} else {
			throw new AnalystError("Unknown cross validation: " + cross);
		}
	}

	/**
	 * Obtain the ML method.
	 * @return The method.
	 */
	private MLMethod obtainMethod() {
		final String resourceID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_MACHINE_LEARNING_FILE);
		final File resourceFile = getScript().resolveFilename(resourceID);

		final MLMethod method = (MLMethod) EncogDirectoryPersistence
				.loadObject(resourceFile);

		if (!(method instanceof MLMethod)) {
			throw new AnalystError(
					"The object to be trained must be an instance of MLMethod. "
							+ method.getClass().getSimpleName());
		}

		return method;
	}

	/**
	 * Obtain the training set.
	 * @return The training set.
	 */
	private MLDataSet obtainTrainingSet() {
		final String trainingID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_TRAINING_FILE);

		final File trainingFile = getScript().resolveFilename(trainingID);

		MLDataSet trainingSet = EncogUtility.loadEGB2Memory(trainingFile);

		if (this.kfold > 0) {
			trainingSet = new FoldedDataSet(trainingSet);
		}

		return trainingSet;
	}

	/**
	 * Perform the training.
	 * @param train The training method.
	 * @param method The ML method.
	 * @param trainingSet The training set.
	 */
	private void performTraining(final MLTrain train, final MLMethod method,
			final MLDataSet trainingSet) {

		ValidateNetwork.validateMethodToData(method, trainingSet);
		final double targetError = getProp().getPropertyDouble(
				ScriptProperties.ML_TRAIN_TARGET_ERROR);
		getAnalyst().reportTrainingBegin();
		final int maxIteration = getAnalyst().getMaxIteration();

		if (train.getImplementationType() == TrainingImplementationType.OnePass) {
			train.iteration();
			getAnalyst().reportTraining(train);
		} else {
			do {
				train.iteration();
				getAnalyst().reportTraining(train);
			} while ((train.getError() > targetError)
					&& !getAnalyst().shouldStopCommand()
					&& !train.isTrainingDone()
					&& ((maxIteration == -1) || (train.getIteration() < maxIteration)));
		}
		train.finishTraining();

		getAnalyst().reportTrainingEnd();
		getAnalyst().setMethod(train.getMethod());
	}

}
