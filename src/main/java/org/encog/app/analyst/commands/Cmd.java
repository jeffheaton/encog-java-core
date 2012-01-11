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

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.prop.ScriptProperties;

/**
 * Base class for Encog Analyst commands. This class defines the properties sent
 * to a command.
 * 
 */
public abstract class Cmd {
	/**
	 * The analyst object that this command belongs to.
	 */
	private final EncogAnalyst analyst;
	
	/**
	 * The script object that this command belongs to.
	 */
	private final AnalystScript script;
	
	/**
	 * The properties to use with this command.
	 */
	private final ScriptProperties properties;

	/**
	 * Construct this command.
	 * @param theAnalyst The analyst that this command belongs to.
	 */
	public Cmd(final EncogAnalyst theAnalyst) {
		this.analyst = theAnalyst;
		this.script = analyst.getScript();
		this.properties = this.script.getProperties();
	}

	/**
	 * Execute this command.
	 * @param args The arguments for this command.
	 * @return True if processing should stop after this command.
	 */
	public abstract boolean executeCommand(String args);

	/**
	 * @return The analyst used with this command.
	 */
	public final EncogAnalyst getAnalyst() {
		return this.analyst;
	}

	/**
	 * @return The name of this command.
	 */
	public abstract String getName();

	/**
	 * @return The properties used with this command.
	 */
	public final ScriptProperties getProp() {
		return this.properties;
	}

	/**
	 * @return The script used with this command.
	 */
	public final AnalystScript getScript() {
		return this.script;
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" name=");
		result.append(getName());
		result.append("]");
		return result.toString();
	}

}
