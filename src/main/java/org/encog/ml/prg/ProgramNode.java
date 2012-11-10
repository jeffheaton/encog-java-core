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
package org.encog.ml.prg;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.prg.expvalue.ExpressionValue;



public abstract class ProgramNode {

	private final String name;
	private final List<ProgramNode> childNodes = new ArrayList<ProgramNode>();
	private final EncogProgram owner;
	private final int[] intData;
	private final ExpressionValue[] expressionData;

	public ProgramNode(final EncogProgram theOwner,
			final String theName, ProgramNode[] theArgs,int intDataSize, int expressionDataSize) {
		this.owner = theOwner;
		this.intData = new int[intDataSize];
		this.expressionData = new ExpressionValue[expressionDataSize];
		this.name = theName;
		this.addChildNodes(theArgs);
		
		for(int i=0;i<this.intData.length;i++) {
			this.intData[i] = 0;
		}
		
		for(int i=0;i<this.expressionData.length;i++) {
			this.expressionData[i] = new ExpressionValue(0);
		}
	}

	public String getName() {
		return this.name;
	}
	
	public abstract ExpressionValue evaluate();
	
	public List<ProgramNode> getChildNodes() {
		return this.childNodes;
	}
	public EncogProgram getOwner() {
		return owner;
	}
	
	public void addChildNodes(ProgramNode[] args) {
		for( ProgramNode pn: args) {
			this.childNodes.add(pn);
		}
	}

	public int[] getIntData() {
		return intData;
	}

	public ExpressionValue[] getExpressionData() {
		return expressionData;
	}
	
	public void randomize(EncogProgram program, double degree) {
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[ProgramNode: name=");
		result.append(this.getName());
		result.append(", childCount=");
		result.append(this.getChildNodes().size());
		result.append(", childNodes=");
		for(ProgramNode node: this.childNodes) {
			result.append(" ");
			result.append(node.getName());
		}
		result.append("]");
		return  result.toString();
	}

	public boolean isVariable() {
		return false;
	}

	public boolean allLeafChildren() {
		boolean result = true;
		
		for(ProgramNode node: this.childNodes) {
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

	public boolean allConstChildren() {
		boolean result = true;
		
		for(ProgramNode node: this.childNodes) {
			if( node.isVariable() ) {
				result = false;
				break;
			}
		}
		
		return result;
	}

	public boolean allConstDescendants() {
		if( this.isVariable() ) {
			return false;
		}
		
		if( this.isLeaf() ) {
			return true;
		}
		
		for(ProgramNode childNode : this.childNodes) {
			if( !childNode.allConstDescendants() ) {
				return false;
			}
		}
		
		return true;
	}
	
	
	
}
