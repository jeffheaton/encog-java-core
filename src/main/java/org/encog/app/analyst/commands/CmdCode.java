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
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.generate.EncogCodeGeneration;
import org.encog.app.generate.TargetLanguage;
import org.encog.util.logging.EncogLogging;

/**
 * This command is used to generate the binary EGB file from a CSV file. The
 * resulting file can be used for training.
 * 
 */
public class CmdCode extends Cmd {

	/**
	 * The name of this command.
	 */
	public static final String COMMAND_NAME = "CODE";

	/**
	 * Construct this generate command.
	 * 
	 * @param analyst
	 *            The analyst to use.
	 */
	public CmdCode(final EncogAnalyst analyst) {
		super(analyst);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean executeCommand(final String args) {
		// get filenames
		final String targetID = getProp().getPropertyString(
				ScriptProperties.CODE_CONFIG_TARGET_FILE);
		final File targetFile = getScript().resolveFilename(targetID);
		
		// get other options
		final TargetLanguage targetLanguage = getProp().getPropertyTargetLanguage(
				ScriptProperties.CODE_CONFIG_TARGET_LANGUAGE);
		final boolean embedData = getProp().getPropertyBoolean(
				ScriptProperties.CODE_CONFIG_EMBED_DATA);

		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Beginning code generation");
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "target file:" + targetID);
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "target language:" + targetLanguage.toString());
		
		EncogCodeGeneration code = new EncogCodeGeneration(targetLanguage);
		code.setEmbedData(embedData);
		code.generate(getAnalyst());		
		code.save(targetFile);
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return CmdCode.COMMAND_NAME;
	}

}
