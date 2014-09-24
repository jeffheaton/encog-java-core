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
package org.encog.parse.expression.common;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;
import org.encog.parse.expression.CommonRender;

/**
 * Render a common expression.
 */
public class RenderCommonExpression extends CommonRender {
	private EncogProgram holder;

	public RenderCommonExpression() {
	}

	public String render(final EncogProgram theHolder) {
		this.holder = theHolder;
		ProgramNode node = theHolder.getRootNode();
		return renderNode(node);
	}

	private String renderConst(ProgramNode node) {
		return node.getData()[0].toStringValue();
	}

	private String renderVar(ProgramNode node) {
		int varIndex = (int)node.getData()[0].toIntValue();
		return this.holder.getVariables().getVariableName(varIndex);
	}
	
	private String renderFunction(ProgramNode node) {
		StringBuilder result = new StringBuilder();
		result.append(node.getName());
		result.append('(');
		for(int i=0;i<node.getChildNodes().size();i++) {
			if( i>0 ) {
				result.append(',');
			}
			ProgramNode childNode = node.getChildNode(i);
			result.append(renderNode(childNode));
		}
		result.append(')');		
		return result.toString();
	}
	
	private String renderOperator(ProgramNode node) {
		StringBuilder result = new StringBuilder();
		result.append("(");
		result.append(renderNode(node.getChildNode(0)));
		result.append(node.getName());
		result.append(renderNode(node.getChildNode(1)));
		result.append(")");
		return result.toString();
	}

	private String renderNode(ProgramNode node) {
		StringBuilder result = new StringBuilder();

		switch (determineNodeType(node)) {
		case ConstVal:
			result.append(renderConst(node));
			break;
		case Operator:
			result.append(renderOperator(node));
			break;
		case Variable:
			result.append(renderVar(node));
			break;
		case Function:
			result.append(renderFunction(node));
			break;
		}

		return result.toString();
	}
}
