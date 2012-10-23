/*
 * Encog(tm) Core v3.2 - Java Version
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
package org.encog.app.generate.program;

import java.util.ArrayList;
import java.util.List;

public class EncogTreeNode {
	private final List<EncogProgramNode> children = new ArrayList<EncogProgramNode>();
	private final EncogTreeNode parent;
	private EncogProgram program;

	public EncogTreeNode(final EncogProgram theProgram,
			final EncogTreeNode theParent) {
		this.program = theProgram;
		this.parent = theParent;
	}

	public void addComment(final String str) {
		final EncogProgramNode node = new EncogProgramNode(this.program, this,
				NodeType.Comment, str);
		this.children.add(node);
	}

	public List<EncogProgramNode> getChildren() {
		return this.children;
	}

	public EncogTreeNode getParent() {
		return this.parent;
	}

	public EncogProgram getProgram() {
		return this.program;
	}

	public void setProgram(final EncogProgram program) {
		this.program = program;
	}

}
