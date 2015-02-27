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
package org.encog.ml.tree.traverse.tasks;

import org.encog.ml.tree.TreeNode;
import org.encog.ml.tree.traverse.DepthFirstTraversal;
import org.encog.ml.tree.traverse.TreeTraversalTask;

/**
 * Count the nodes in an acyclic tree.
 */
public class TaskCountNodes implements TreeTraversalTask {
	/**
	 * The count so far.
	 */
	private int nodeCount;
	
	/**
	 * Construct the task.
	 */
	public TaskCountNodes() {
		this.nodeCount = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean task(TreeNode node) {
		this.nodeCount++;
		return true;
	}

	/**
	 * @return The current node count.
	 */
	public int getNodeCount() {
		return nodeCount;
	}

	/**
	 * Set the current node count.
	 * @param nodeCount The node count.
	 */
	public void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}
	
	/**
	 * Count the nodes from this tree node.
	 * @param node The tree node.
	 * @return The node count.
	 */
	public static int process(TreeNode node) {
		TaskCountNodes task = new TaskCountNodes();
		DepthFirstTraversal trav = new DepthFirstTraversal();
		trav.traverse(node, task);
		return task.getNodeCount();
	}

}
