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
 * Get a node by index.
 */
public class TaskGetNodeIndex implements TreeTraversalTask {
	/**
	 * The running node count.
	 */
	private int nodeCount;
	
	/**
	 * The index that we are seeking.
	 */
	private int targetIndex;
	
	/**
	 * The resulting node at the specified index.
	 */
	private TreeNode result;
	
	public TaskGetNodeIndex(int theIndex) {
		this.targetIndex = theIndex;
		this.nodeCount = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean task(TreeNode node) {
		if( this.nodeCount>=targetIndex) {
			if( result==null ) {
				result = node;
			}
			return false;
		}
		
		this.nodeCount++;
		return true;
	}
	
	/**
	 * @return The resulting tree node.
	 */
	public TreeNode getResult() {
		return result;
	}

	/**
	 * Obtain the specified tree node for the specified index.
	 * @param index The index.
	 * @param node The tree node to search from.
	 * @return The tree node for the specified index.
	 */
	public static TreeNode process(int index, TreeNode node) {
		TaskGetNodeIndex task = new TaskGetNodeIndex(index);
		DepthFirstTraversal trav = new DepthFirstTraversal();
		trav.traverse(node, task);
		return task.getResult();
	}

}
