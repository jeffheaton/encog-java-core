/*
 * Encog(tm) Core v3.2 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2013 Heaton Research, Inc.
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

import org.encog.ml.prg.ProgramNode;
import org.encog.ml.tree.TreeNode;
import org.encog.ml.tree.traverse.DepthFirstTraversal;
import org.encog.ml.tree.traverse.TreeTraversalTask;

public class TaskReplaceNode implements TreeTraversalTask {
	
	private final ProgramNode replaceThisNode; 
	private final ProgramNode replaceWith;
	private boolean done;
	
	public TaskReplaceNode(ProgramNode theReplaceThisNode, ProgramNode theReplaceWith) {
		this.replaceThisNode = theReplaceThisNode;
		this.replaceWith = theReplaceWith;
		this.done = false;
	}
	
	
	
	public ProgramNode getReplaceThisNode() {
		return replaceThisNode;
	}



	public ProgramNode getReplaceWith() {
		return replaceWith;
	}



	@Override
	public boolean task(TreeNode node) {
		if( done ) {
			return false;
		}
		
		for(int i=0;i<node.getChildNodes().size();i++) {
			TreeNode childNode = node.getChildNodes().get(i);
			if( childNode==replaceThisNode) {
				node.getChildNodes().set(i, this.replaceWith);
				done = true;
				return false;
			}
		}
		return true;
	}
	
	public static void process(ProgramNode rootNode, ProgramNode replaceThisNode, ProgramNode replaceWith) {
		TaskReplaceNode task = new TaskReplaceNode(replaceThisNode, replaceWith);
		DepthFirstTraversal trav = new DepthFirstTraversal();
		trav.traverse(rootNode, task);
	}

}
