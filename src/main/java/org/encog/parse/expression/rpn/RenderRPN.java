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
package org.encog.parse.expression.rpn;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;
import org.encog.parse.expression.CommonRender;
import org.encog.parse.expression.ExpressionNodeType;

public class RenderRPN extends CommonRender {
	private EncogProgram program;

	public RenderRPN() {
	}

	public String render(final EncogProgram theProgram) {
		this.program = theProgram;
		return renderNode(this.program.getRootNode());
	}
	
	private String handleConst(ProgramNode node) {
		return node.getData()[0].toStringValue();
	}
	
	private String handleVar(ProgramNode node) {
		int varIndex = (int)node.getData()[0].toIntValue();
		return this.program.getVariables().getVariableName(varIndex);
	}
	
	

	private String renderNode(ProgramNode node) {
		StringBuilder result = new StringBuilder();
		
		ExpressionNodeType t = this.determineNodeType(node);
		
		for(int i=0;i<node.getChildNodes().size();i++) {
			ProgramNode childNode = node.getChildNode(i);
			if( result.length()>0 ) {
				result.append(" ");
			}
			result.append(renderNode(childNode));
		}

		if( result.length()>0 ) {
			result.append(" ");
		}
		
		if( t==ExpressionNodeType.ConstVal ) {
			result.append(handleConst(node));
		} else if( t==ExpressionNodeType.Variable ) {
			result.append(handleVar(node));
		} else if( t==ExpressionNodeType.Function || t==ExpressionNodeType.Operator) {
			result.append('[');
			result.append(node.getName());
			
			result.append(']');
		}
				
		return result.toString().trim();
	}
	
}
