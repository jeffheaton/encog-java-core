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
package org.encog.ml.tree.basic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.encog.ml.tree.TreeNode;
import org.encog.ml.tree.traverse.tasks.TaskCountNodes;

public class BasicTreeNode implements TreeNode, Serializable {
	private final List<TreeNode> childNodes = new ArrayList<TreeNode>();
	
	public List<TreeNode> getChildNodes() {
		return this.childNodes;
	}
	
	public void addChildNodes(TreeNode[] args) {
		for( TreeNode pn: args) {
			this.childNodes.add(pn);
		}
	}
	
	public boolean allLeafChildren() {
		boolean result = true;
		
		for(TreeNode node: this.childNodes) {
			if( !node.isLeaf() ) {
				result = false;
				break;
			}
		}
		
		return result;
	}

	public boolean isLeaf() {
		return this.childNodes.size()==0;
	}

	@Override
	public int size() {
		return TaskCountNodes.process(this);
	}
	
}
