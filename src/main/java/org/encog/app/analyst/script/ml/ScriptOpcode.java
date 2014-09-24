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
package org.encog.app.analyst.script.ml;

import org.encog.ml.prg.extension.ProgramExtensionTemplate;

/**
 * An opcode, stored in the script.
 */
public class ScriptOpcode {

	/**
	 * The name of the opcode.
	 */
	private final String name;

	/**
	 * The argument count of the opcode.
	 */
	private final int argCount;

	public ScriptOpcode(final ProgramExtensionTemplate temp) {
		this(temp.getName(), temp.getChildNodeCount());
	}

	/**
	 * Construct the opcode.
	 * 
	 * @param name
	 *            The name of the opcode.
	 * @param argCount
	 *            The argument count.
	 */
	public ScriptOpcode(final String name, final int argCount) {
		super();
		this.name = name;
		this.argCount = argCount;
	}

	/**
	 * @return the argCount
	 */
	public int getArgCount() {
		return this.argCount;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

}
