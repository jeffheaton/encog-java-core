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
	private final ExpressionValue[] doubleData;

	public ProgramNode(final EncogProgram theOwner,
			final String theName, ProgramNode[] theArgs,int intDataSize, int expressionDataSize) {
		this.owner = theOwner;
		this.intData = new int[intDataSize];
		this.doubleData = new ExpressionValue[expressionDataSize];
		this.name = theName;
		this.addChildNodes(theArgs);
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
		return doubleData;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[ProgramNode: name=");
		result.append(this.getName());
		result.append(", childCount=");
		result.append(this.getChildNodes().size());
		result.append("]");
		return  result.toString();
	}
	
	
	
}
