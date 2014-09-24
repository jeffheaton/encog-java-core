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
package org.encog.ml.prg;

import java.io.Serializable;

import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.tree.TreeNode;
import org.encog.ml.tree.basic.BasicTreeNode;

/**
 * Represents a program node in an EPL program.
 */
public class ProgramNode extends BasicTreeNode implements Serializable {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The opcode that this node implements.
	 */
	private final ProgramExtensionTemplate template;

	/**
	 * The Encog program that this node belongs to.
	 */
	private final EncogProgram owner;

	/**
	 * Any data associated with this node. For example, const nodes will store
	 * their value here.
	 */
	private final ExpressionValue[] data;

	/**
	 * Construct the program node.
	 * 
	 * @param theOwner
	 *            The owner of the node.
	 * @param theTemplate
	 *            The opcode that this node is based on.
	 * @param theArgs
	 *            The child nodes to this node.
	 */
	public ProgramNode(final EncogProgram theOwner,
			final ProgramExtensionTemplate theTemplate,
			final ProgramNode[] theArgs) {
		this.owner = theOwner;
		this.data = new ExpressionValue[theTemplate.getDataSize()];
		this.template = theTemplate;
		addChildNodes(theArgs);

		for (int i = 0; i < this.data.length; i++) {
			this.data[i] = new ExpressionValue(0);
		}
	}

	/**
	 * @return True if all children are constant.
	 */
	public boolean allConstChildren() {
		boolean result = true;

		for (final TreeNode tn : getChildNodes()) {
			final ProgramNode node = (ProgramNode) tn;
			if (node.isVariable()) {
				result = false;
				break;
			}
		}

		return result;
	}

	/**
	 * @return True if all descendants are constant.
	 */
	public boolean allConstDescendants() {
		if (isVariable()) {
			return false;
		}

		if (isLeaf()) {
			return true;
		}

		for (final TreeNode tn : getChildNodes()) {
			final ProgramNode childNode = (ProgramNode) tn;
			if (!childNode.allConstDescendants()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @return The evaluated value of this node.
	 */
	public ExpressionValue evaluate() {
		return this.template.evaluate(this);
	}

	/**
	 * Get the specified child node.
	 * 
	 * @param index
	 *            The index of this node.
	 * @return The child node requested.
	 */
	public ProgramNode getChildNode(final int index) {
		return (ProgramNode) getChildNodes().get(index);
	}

	/**
	 * @return The node data.
	 */
	public ExpressionValue[] getData() {
		return this.data;
	}

	/**
	 * @return The name of this node (from the opcode template).
	 */
	public String getName() {
		return this.template.getName();
	}

	/**
	 * @return The EncogProgram that owns this node.
	 */
	public EncogProgram getOwner() {
		return this.owner;
	}

	/**
	 * @return the template
	 */
	public ProgramExtensionTemplate getTemplate() {
		return this.template;
	}

	/**
	 * @return Returns true if this node's value is variable.
	 */
	public boolean isVariable() {
		return this.template.isVariable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[ProgramNode: name=");
		result.append(this.template.getName());
		result.append(", childCount=");
		result.append(getChildNodes().size());
		result.append(", childNodes=");
		for (final TreeNode tn : getChildNodes()) {
			final ProgramNode node = (ProgramNode) tn;
			result.append(" ");
			result.append(node.getTemplate().getName());
		}
		result.append("]");
		return result.toString();
	}

}
