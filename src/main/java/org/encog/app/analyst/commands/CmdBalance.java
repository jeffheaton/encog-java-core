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
import org.encog.app.analyst.csv.balance.BalanceCSV;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.util.AnalystReportBridge;
import org.encog.util.csv.CSVFormat;
import org.encog.util.logging.EncogLogging;

/**
 * Performs the balance command. This allows large classes to have members
 * discarded.
 * 
 */
public class CmdBalance extends Cmd {

	/**
	 * The name of this command.
	 */
	public static final String COMMAND_NAME = "BALANCE";

	/**
	 * Construct the balance command.
	 * 
	 * @param analyst
	 *            The analyst to use with this command.
	 */
	public CmdBalance(final EncogAnalyst analyst) {
		super(analyst);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean executeCommand(final String args) {
		// get filenames
		final String sourceID = getProp().getPropertyString(
				ScriptProperties.BALANCE_CONFIG_SOURCE_FILE);
		final String targetID = getProp().getPropertyString(
				ScriptProperties.BALANCE_CONFIG_TARGET_FILE);
		
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, 
			"Beginning balance");
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, 
			"source file:" + sourceID);
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, 
			"target file:" + targetID);

		final File sourceFile = getScript().resolveFilename(sourceID);
		final File targetFile = getScript().resolveFilename(targetID);

		// get other config data
		final int countPer = getProp().getPropertyInt(
				ScriptProperties.BALANCE_CONFIG_COUNT_PER);
		final String targetFieldStr = getProp().getPropertyString(
				ScriptProperties.BALANCE_CONFIG_BALANCE_FIELD);
		final DataField targetFieldDF = getAnalyst().getScript().findDataField(
				targetFieldStr);
		if (targetFieldDF == null) {
			throw new AnalystError("Can't find balance target field: "
					+ targetFieldStr);
		}
		if (!targetFieldDF.isClass()) {
			throw new AnalystError("Can't balance on non-class field: "
					+ targetFieldStr);
		}

		final int targetFieldIndex = getAnalyst().getScript()
				.findDataFieldIndex(targetFieldDF);

		// mark generated
		getScript().markGenerated(targetID);

		// get formats
		final CSVFormat format = getScript().determineFormat();

		// prepare to normalize
		final BalanceCSV balance = new BalanceCSV();
		balance.setScript(getScript());
		getAnalyst().setCurrentQuantTask(balance);
		balance.setReport(new AnalystReportBridge(getAnalyst()));

		final boolean headers = getScript().expectInputHeaders(sourceID);
		balance.analyze(sourceFile, headers, format);
		balance.setProduceOutputHeaders(true);
		balance.process(targetFile, targetFieldIndex, countPer);
		getAnalyst().setCurrentQuantTask(null);
		return balance.shouldStop();
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public final String getName() {
		return CmdBalance.COMMAND_NAME;
	}

}
