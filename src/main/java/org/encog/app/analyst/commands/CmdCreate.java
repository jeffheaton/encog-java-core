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

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.ml.MLMethod;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.data.buffer.EncogEGBFile;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.logging.EncogLogging;

/**
 * The Encog Analyst create command. This command is used to create a Machine
 * Learning method.
 * 
 */
public class CmdCreate extends Cmd {

	/**
	 * The name of this command.
	 */
	public static final String COMMAND_NAME = "CREATE";

	/**
	 * Construct the create command.
	 * @param theAnalyst The analyst to use.
	 */
	public CmdCreate(final EncogAnalyst theAnalyst) {
		super(theAnalyst);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean executeCommand(final String args) {
		// get filenames
		final String trainingID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_TRAINING_FILE);
		final String resourceID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_MACHINE_LEARNING_FILE);

		final File trainingFile = getScript().resolveFilename(trainingID);
		final File resourceFile = getScript().resolveFilename(resourceID);

		final String type = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_TYPE);
		final String arch = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_ARCHITECTURE);
				
			EncogLogging.log(EncogLogging.LEVEL_DEBUG, 
				"Beginning create");
			EncogLogging.log(EncogLogging.LEVEL_DEBUG, 
					"training file:" + trainingID);
			EncogLogging.log(EncogLogging.LEVEL_DEBUG, 
					"resource file:" + resourceID);	
			EncogLogging.log(EncogLogging.LEVEL_DEBUG, 
					"type:" + type);
			EncogLogging.log(EncogLogging.LEVEL_DEBUG, 
					"arch:" + arch);	

		final EncogEGBFile egb = new EncogEGBFile(trainingFile);
		egb.open();
		final int input = egb.getInputCount();
		final int ideal = egb.getIdealCount();
		egb.close();

		final MLMethodFactory factory = new MLMethodFactory();
		final MLMethod obj = factory.create(type, arch, input, ideal);
		
		if( obj instanceof BayesianNetwork ) {
			final String query = getProp().getPropertyString(
					ScriptProperties.ML_CONFIG_QUERY);
			((BayesianNetwork)obj).defineClassificationStructure(query);			
		}

		EncogDirectoryPersistence.saveObject(resourceFile, obj);

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getName() {
		return CmdCreate.COMMAND_NAME;
	}

}
