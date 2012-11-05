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
	public abstract ExpressionValue evaluate();
	
	private final List<ProgramNode> childNodes = new ArrayList<ProgramNode>();
	private final EncogProgram owner;
	private final int[] intData;
	private final double[] doubleData;
	
	public ProgramNode(EncogProgram theOwner,int intDataSize, int doubleDataSize) {
		this.owner = theOwner;
		this.intData = new int[intDataSize];
		this.doubleData = new double[doubleDataSize];
	}
	
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

	public double[] getDoubleData() {
		return doubleData;
	}
	
	
}
