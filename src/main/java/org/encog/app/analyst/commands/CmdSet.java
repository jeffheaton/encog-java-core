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

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.prop.PropertyConstraints;
import org.encog.app.analyst.script.prop.PropertyEntry;

/**
 * The set command allows a script to override a property value.
 */
public class CmdSet extends Cmd {

	/**
	 * The name of this command.
	 */
	public static final String COMMAND_NAME = "SET";

	/**
	 * Construct the set command with the analyst.
	 * @param analyst The analyst to use.
	 */
	public CmdSet(final EncogAnalyst analyst) {
		super(analyst);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean executeCommand(final String args) {
		final int index = args.indexOf('=');
		final String dots = args.substring(0, index).trim();
		String value = args.substring(index + 1).trim();

		final PropertyEntry entry = PropertyConstraints.getInstance()
				.findEntry(dots);

		if (entry == null) {
			throw new AnalystError("Unknown property: " + args.toUpperCase());
		}

		// strip quotes
		if (value.charAt(0) == '\"') {
			value = value.substring(1);
		}
		if (value.endsWith("\"")) {
			value = value.substring(0, value.length() - 1);
		}

		final String[] cols = dots.split("\\.");
		final String section = cols[0];
		final String subSection = cols[1];
		final String name = cols[2];

		entry.validate(section, subSection, name, value);
		getProp().setProperty(entry.getKey(), value);

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getName() {
		return CmdSet.COMMAND_NAME;
	}

}
