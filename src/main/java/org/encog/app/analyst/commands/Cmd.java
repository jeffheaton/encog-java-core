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
package org.encog.app.analyst.commands;

import java.io.File;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.simple.EncogUtility;

/**
 * Base class for Encog Analyst commands. This class defines the properties sent
 * to a command.
 * 
 */
public abstract class Cmd {
	
	/**
	 * The number of folds, if kfold is used.
	 */
	private int kfold;
	
	/**
	 * The analyst object that this command belongs to.
	 */
	private final EncogAnalyst analyst;
	
	/**
	 * The script object that this command belongs to.
	 */
	private final AnalystScript script;
	
	/**
	 * The properties to use with this command.
	 */
	private final ScriptProperties properties;

	/**
	 * Construct this command.
	 * @param theAnalyst The analyst that this command belongs to.
	 */
	public Cmd(final EncogAnalyst theAnalyst) {
		this.analyst = theAnalyst;
		this.script = analyst.getScript();
		this.properties = this.script.getProperties();
	}

	/**
	 * Execute this command.
	 * @param args The arguments for this command.
	 * @return True if processing should stop after this command.
	 */
	public abstract boolean executeCommand(String args);

	/**
	 * @return The analyst used with this command.
	 */
	public EncogAnalyst getAnalyst() {
		return this.analyst;
	}

	/**
	 * @return The name of this command.
	 */
	public abstract String getName();

	/**
	 * @return The properties used with this command.
	 */
	public ScriptProperties getProp() {
		return this.properties;
	}

	/**
	 * @return The script used with this command.
	 */
	public AnalystScript getScript() {
		return this.script;
	}
	
	/**
	 * Obtain the number of folds for cross validation.
	 * @return The number of folds.
	 */
	public int obtainCross() {
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
	public MLMethod obtainMethod() {
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
	public MLDataSet obtainTrainingSet() {
		final String trainingID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_TRAINING_FILE);

		final File trainingFile = getScript().resolveFilename(trainingID);

		MLDataSet trainingSet = EncogUtility.loadEGB2Memory(trainingFile);

		if (this.kfold > 0) {
			trainingSet = new FoldedDataSet(trainingSet);
		}

		return trainingSet;
	}
	
	
	public int getKfold() {
		return kfold;
	}

	public void setKfold(int kfold) {
		this.kfold = kfold;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" name=");
		result.append(getName());
		result.append("]");
		return result.toString();
	}

}
