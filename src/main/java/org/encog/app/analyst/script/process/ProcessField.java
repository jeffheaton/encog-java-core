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
package org.encog.app.analyst.script.process;

/**
 * Holds one field for Encog analyst preprocess.
 */
public class ProcessField {
	/**
	 * The name of this field.
	 */
	private final String name;
	
	/**
	 * The command for this field.
	 */
	private final String command;
	
	/**
	 * Construct this field.
	 * @param name The name of this field.
	 * @param command The command for this field.
	 */
	public ProcessField(String name, String command) {
		this.name = name;
		this.command = command;
	}

	/**
	 * @return The name of this field.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The command for this field.
	 */
	public String getCommand() {
		return command;
	}
	
	
}
