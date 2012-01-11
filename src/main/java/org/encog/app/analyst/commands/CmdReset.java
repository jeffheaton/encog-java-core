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

import java.util.Map;

import org.encog.app.analyst.EncogAnalyst;

/**
 * Analyst command that allows all properties to be reset to what they were
 * originally loaded from the Encog EGA file.
 * 
 */
public class CmdReset extends Cmd {

	/**
	 * The name of this command.
	 */
	public static final String COMMAND_NAME = "RESET";

	/**
	 * Construct the reset command.
	 * 
	 * @param analyst
	 *            The analyst to use.
	 */
	public CmdReset(final EncogAnalyst analyst) {
		super(analyst);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean executeCommand(final String args) {
		final Map<String, String> revertedData = getAnalyst().getRevertData();
		getScript().getProperties().performRevert(revertedData);
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getName() {
		return CmdReset.COMMAND_NAME;
	}

}
