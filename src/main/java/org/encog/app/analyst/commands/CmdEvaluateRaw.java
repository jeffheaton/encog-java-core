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
import org.encog.app.analyst.csv.AnalystEvaluateRawCSV;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.util.AnalystReportBridge;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.logging.EncogLogging;

/**
 * This class is used to evaluate a machine learning method. Evaluation data is
 * provided and the ideal and actual responses from the machine learning method
 * are written to a file.
 * 
 */
public class CmdEvaluateRaw extends Cmd {

	/**
	 * The name of the command.
	 */
	public static final String COMMAND_NAME = "EVALUATE-RAW";

	/**
	 * Construct an evaluate raw command.
	 * @param analyst The analyst object to use.
	 */
	public CmdEvaluateRaw(final EncogAnalyst analyst) {
		super(analyst);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean executeCommand(final String args) {
		// get filenames
		final String evalID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_EVAL_FILE);
		final String resourceID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_MACHINE_LEARNING_FILE);

		final String outputID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_OUTPUT_FILE);
		
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, 
			"Beginning evaluate raw");
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, 
			"evaluate file:" + evalID);
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, 
			"resource file:" + resourceID);

		final File evalFile = getScript().resolveFilename(evalID);
		final File resourceFile = getScript().resolveFilename(resourceID);

		final File outputFile = getAnalyst().getScript().resolveFilename(
				outputID);

		MLMethod m = (MLMethod) EncogDirectoryPersistence.loadObject(resourceFile);
		
		if( !(m instanceof MLRegression) ) {
			throw new AnalystError("The evaluate raw command can only be used with regression.");
		}

		final MLRegression method = (MLRegression)m;

		final boolean headers = getScript().expectInputHeaders(evalID);

		final AnalystEvaluateRawCSV eval = new AnalystEvaluateRawCSV();
		eval.setScript(getScript());
		getAnalyst().setCurrentQuantTask(eval);
		eval.setReport(new AnalystReportBridge(getAnalyst()));
		eval.analyze(getAnalyst(), evalFile, headers, getProp()
				.getPropertyCSVFormat(
						ScriptProperties.SETUP_CONFIG_CSV_FORMAT));
		eval.process(outputFile, method);
		getAnalyst().setCurrentQuantTask(null);
		return eval.shouldStop();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getName() {
		return CmdEvaluateRaw.COMMAND_NAME;
	}

}
