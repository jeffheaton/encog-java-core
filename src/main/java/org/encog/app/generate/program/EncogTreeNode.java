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

import java.util.ArrayList;
import java.util.List;

/**
 * A tree node that represents code to be generated.
 * 
 */
public class EncogTreeNode {

	/**
	 * The child nodes.
	 */
	private final List<EncogProgramNode> children = new ArrayList<EncogProgramNode>();

	/**
	 * The parent node.
	 */
	private final EncogTreeNode parent;

	/**
	 * The program that this node belogs to.
	 */
	private EncogGenProgram program;

	/**
	 * Construct a tree node.
	 * 
	 * @param theProgram
	 *            The program.
	 * @param theParent
	 *            The parent.
	 */
	public EncogTreeNode(final EncogGenProgram theProgram,
			final EncogTreeNode theParent) {
		this.program = theProgram;
		this.parent = theParent;
	}

	/**
	 * Add a comment.
	 * 
	 * @param str
	 *            The comment.
	 */
	public void addComment(final String str) {
		final EncogProgramNode node = new EncogProgramNode(this.program, this,
				NodeType.Comment, str);
		this.children.add(node);
	}

	/**
	 * @return The children.
	 */
	public List<EncogProgramNode> getChildren() {
		return this.children;
	}

	/**
	 * @return The parent.
	 */
	public EncogTreeNode getParent() {
		return this.parent;
	}

	/**
	 * @return The program.
	 */
	public EncogGenProgram getProgram() {
		return this.program;
	}

	/**
	 * Set the program.
	 * 
	 * @param program
	 *            The program.
	 */
	public void setProgram(final EncogGenProgram program) {
		this.program = program;
	}

}
