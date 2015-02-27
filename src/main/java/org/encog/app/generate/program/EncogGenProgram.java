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
package org.encog.app.generate.program;

/**
 * Holds a generated Encog program. A language specific generator will take this
 * and generate actual source code from it.
 */
public class EncogGenProgram extends EncogTreeNode {

	/**
	 * Construct the program.
	 */
	public EncogGenProgram() {
		super(null, null);
		setProgram(this);
	}

	/**
	 * Create a new class.
	 * 
	 * @param className
	 *            The class name.
	 * @return The newly created class.
	 */
	public EncogProgramNode createClass(final String className) {
		final EncogProgramNode node = new EncogProgramNode(this, this,
				NodeType.Class, className);
		getChildren().add(node);
		return node;
	}

}
