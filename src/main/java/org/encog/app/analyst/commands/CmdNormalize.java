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
import org.encog.app.analyst.csv.normalize.AnalystNormalizeCSV;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.util.AnalystReportBridge;
import org.encog.util.csv.CSVFormat;
import org.encog.util.logging.EncogLogging;

/**
 * The normalize command is used to normalize data. Data normalization generally
 * maps values from one number range to another, typically to -1 to 1.
 * 
 */
public class CmdNormalize extends Cmd {

	/**
	 * The name of this command.
	 */
	public static final String COMMAND_NAME = "NORMALIZE";

	/**
	 * Construct the normalize command.
	 * 
	 * @param theAnalyst
	 *            The analyst to use.
	 */
	public CmdNormalize(final EncogAnalyst theAnalyst) {
		super(theAnalyst);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean executeCommand(final String args) {
		// get filenames
		final String sourceID = getProp().getPropertyString(
				ScriptProperties.NORMALIZE_CONFIG_SOURCE_FILE);
		final String targetID = getProp().getPropertyString(
				ScriptProperties.NORMALIZE_CONFIG_TARGET_FILE);

		final File sourceFile = getScript().resolveFilename(sourceID);
		final File targetFile = getScript().resolveFilename(targetID);

		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Beginning normalize");
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "source file:" + sourceID);
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "target file:" + targetID);

		// mark generated
		getScript().markGenerated(targetID);

		// get formats
		final CSVFormat format = getScript().determineFormat();

		// prepare to normalize
		final AnalystNormalizeCSV norm = new AnalystNormalizeCSV();
		norm.setScript(getScript());
		getAnalyst().setCurrentQuantTask(norm);
		norm.setReport(new AnalystReportBridge(getAnalyst()));

		final boolean headers = getScript().expectInputHeaders(sourceID);
		norm.analyze(sourceFile, headers, format, getAnalyst());
		norm.setProduceOutputHeaders(true);
		norm.normalize(targetFile);
		getAnalyst().setCurrentQuantTask(null);
		return norm.shouldStop();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getName() {
		return CmdNormalize.COMMAND_NAME;
	}

}
