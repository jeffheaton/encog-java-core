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
import org.encog.app.analyst.csv.shuffle.ShuffleCSV;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.util.AnalystReportBridge;
import org.encog.util.csv.CSVFormat;
import org.encog.util.logging.EncogLogging;

/**
 * This command is used to randomize the lines in a CSV file.
 * 
 */
public class CmdRandomize extends Cmd {

	/**
	 * The name of the command.
	 */
	public static final String COMMAND_NAME = "RANDOMIZE";

	/**
	 * Construct the randomize command.
	 * 
	 * @param analyst
	 *            The analyst to use.
	 */
	public CmdRandomize(final EncogAnalyst analyst) {
		super(analyst);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean executeCommand(final String args) {
		// get filenames
		final String sourceID = getProp().getPropertyString(
				ScriptProperties.RANDOMIZE_CONFIG_SOURCE_FILE);
		final String targetID = getProp().getPropertyString(
				ScriptProperties.RANDOMIZE_CONFIG_TARGET_FILE);

		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Beginning randomize");
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "source file:" + sourceID);
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "target file:" + targetID);

		final File sourceFile = getScript().resolveFilename(sourceID);
		final File targetFile = getScript().resolveFilename(targetID);

		// get formats
		final CSVFormat format = getScript().determineFormat();

		// mark generated
		getScript().markGenerated(targetID);

		// prepare to normalize
		final ShuffleCSV norm = new ShuffleCSV();
		norm.setScript(getScript());
		getAnalyst().setCurrentQuantTask(norm);
		norm.setReport(new AnalystReportBridge(getAnalyst()));
		final boolean headers = getScript().expectInputHeaders(sourceID);
		norm.analyze(sourceFile, headers, format);
		norm.process(targetFile);
		getAnalyst().setCurrentQuantTask(null);
		return norm.shouldStop();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getName() {
		return CmdRandomize.COMMAND_NAME;
	}

}
