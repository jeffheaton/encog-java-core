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
import java.util.ArrayList;
import java.util.List;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.util.CSVHeaders;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.util.csv.CSVFormat;
import org.encog.util.logging.EncogLogging;
import org.encog.util.simple.EncogUtility;

/**
 * This command is used to generate the binary EGB file from a CSV file. The
 * resulting file can be used for training.
 * 
 */
public class CmdGenerate extends Cmd {

	/**
	 * The name of this command.
	 */
	public static final String COMMAND_NAME = "GENERATE";

	/**
	 * Construct this generate command.
	 * 
	 * @param analyst
	 *            The analyst to use.
	 */
	public CmdGenerate(final EncogAnalyst analyst) {
		super(analyst);
	}

	/**
	 * Determine the ideal fields.
	 * 
	 * @param headerList
	 *            The headers.
	 * @return The indexes of the ideal fields.
	 */
	private int[] determineIdealFields(final CSVHeaders headerList) {

		int[] result;
		final String type = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_TYPE);

		// is it non-supervised?
		if (type.equals(MLMethodFactory.TYPE_SOM)) {
			result = new int[0];
			return result;
		}

		final List<Integer> fields = new ArrayList<Integer>();

		for (int currentIndex = 0; currentIndex < headerList.size(); currentIndex++) {
			final String baseName = headerList.getBaseHeader(currentIndex);
			final int slice = headerList.getSlice(currentIndex);
			final AnalystField field = getAnalyst().getScript()
					.findNormalizedField(baseName, slice);

			if (field != null && field.isOutput()) {
				fields.add(currentIndex);
			}
		}

		// allocate result array
		result = new int[fields.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = fields.get(i);
		}

		return result;
	}

	/**
	 * Determine the input fields.
	 * 
	 * @param headerList
	 *            The headers.
	 * @return The indexes of the input fields.
	 */
	private int[] determineInputFields(final CSVHeaders headerList) {
		final List<Integer> fields = new ArrayList<Integer>();

		for (int currentIndex = 0; currentIndex < headerList.size(); currentIndex++) {
			final String baseName = headerList.getBaseHeader(currentIndex);
			final int slice = headerList.getSlice(currentIndex);
			final AnalystField field = getAnalyst().getScript()
					.findNormalizedField(baseName, slice);

			if (field != null && field.isInput()) {
				fields.add(currentIndex);
			}
		}

		// allocate result array
		final int[] result = new int[fields.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = fields.get(i);
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean executeCommand(final String args) {
		// get filenames
		final String sourceID = getProp().getPropertyString(
				ScriptProperties.GENERATE_CONFIG_SOURCE_FILE);
		final String targetID = getProp().getPropertyString(
				ScriptProperties.GENERATE_CONFIG_TARGET_FILE);
		final CSVFormat format = getAnalyst().getScript().determineFormat();

		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Beginning generate");
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "source file:" + sourceID);
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "target file:" + targetID);

		final File sourceFile = getScript().resolveFilename(sourceID);
		final File targetFile = getScript().resolveFilename(targetID);

		// mark generated
		getScript().markGenerated(targetID);

		// read file
		final boolean headers = getScript().expectInputHeaders(sourceID);
		final CSVHeaders headerList = new CSVHeaders(sourceFile, headers,
				format);

		final int[] input = determineInputFields(headerList);
		final int[] ideal = determineIdealFields(headerList);

		EncogUtility.convertCSV2Binary(sourceFile, format, targetFile, input,
				ideal, headers);
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getName() {
		return CmdGenerate.COMMAND_NAME;
	}

}
