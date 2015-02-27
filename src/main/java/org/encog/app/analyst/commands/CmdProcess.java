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

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.csv.process.AnalystProcess;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.util.AnalystReportBridge;
import org.encog.util.csv.CSVFormat;
import org.encog.util.logging.EncogLogging;

/**
 * This command is used to preprocess a CSV file.
 * 
 */
public class CmdProcess extends Cmd {

	/**
	 * The name of the command.
	 */
	public static final String COMMAND_NAME = "PROCESS";

	/**
	 * Construct the randomize command.
	 * 
	 * @param analyst
	 *            The analyst to use.
	 */
	public CmdProcess(final EncogAnalyst analyst) {
		super(analyst);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean executeCommand(final String args) {
		// get filenames
		final String sourceID = getProp().getPropertyString(
				ScriptProperties.PROCESS_CONFIG_SOURCE_FILE);
		final String targetID = getProp().getPropertyString(
				ScriptProperties.PROCESS_CONFIG_TARGET_FILE);
		
		final int forwardSize = getProp().getPropertyInt(
				ScriptProperties.PROCESS_CONFIG_FORWARD_SIZE);
		final int backwardSize = getProp().getPropertyInt(
				ScriptProperties.PROCESS_CONFIG_BACKWARD_SIZE);

		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Beginning randomize");
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "source file:" + sourceID);
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "target file:" + targetID);

		final File sourceFile = getScript().resolveFilename(sourceID);
		final File targetFile = getScript().resolveFilename(targetID);

		// get formats
		final CSVFormat format = getScript().determineFormat();

		// mark generated
		getScript().markGenerated(targetID);

		// prepare to transform
		final AnalystProcess process = new AnalystProcess(getAnalyst(),backwardSize,forwardSize);
		process.setScript(getScript());
		getAnalyst().setCurrentQuantTask(process);
		process.setReport(new AnalystReportBridge(getAnalyst()));
		final boolean headers = getScript().expectInputHeaders(sourceID);
		process.analyze(sourceFile, headers, format);
		process.process(targetFile);
		getAnalyst().setCurrentQuantTask(null);
		return process.shouldStop();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return CmdProcess.COMMAND_NAME;
	}

}
