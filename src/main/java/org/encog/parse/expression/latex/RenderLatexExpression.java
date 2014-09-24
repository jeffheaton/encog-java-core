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
package org.encog.parse.expression.latex;

import org.encog.ml.ea.exception.EACompileError;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.NodeType;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.parse.expression.ExpressionNodeType;

// "x=\\frac{-b \\pm \\sqrt {b^2-4ac}}{2a}";

public class RenderLatexExpression {
	private EncogProgram program;

	public RenderLatexExpression() {
	}

	public String render(final EncogProgram theProgram) {
		this.program = theProgram;
		return renderNode(this.program.getRootNode());
	}

	private String handleConst(ProgramNode node) {
		ExpressionValue v = node.getData()[0];
		return v.toStringValue();
	}

	private String handleVar(ProgramNode node) {
		int varIndex = (int)node.getData()[0].toIntValue();
		return this.program.getVariables().getVariableName(varIndex);
	}

	private String handleFunction(ProgramNode node) {
		ProgramExtensionTemplate temp = node.getTemplate();
		StringBuilder result = new StringBuilder();

		if (temp == StandardExtensions.EXTENSION_SQRT) {
			result.append("\\sqrt{");
			result.append(renderNode(node.getChildNode(0)));
			result.append("}");
		} else {
			result.append(temp.getName());
			result.append('(');
			for (int i = 0; i < temp.getChildNodeCount(); i++) {
				if (i > 0) {
					result.append(',');
				}
				result.append(renderNode(node.getChildNode(i)));
			}
			result.append(')');
		}
		return result.toString();
	}

	private String handleOperator(ProgramNode node) {
		ProgramExtensionTemplate temp = node.getTemplate();
		StringBuilder result = new StringBuilder();

		if (temp.getChildNodeCount() == 2) {
			String a = renderNode(node.getChildNode(0));
			String b = renderNode(node.getChildNode(1));

			if (temp == StandardExtensions.EXTENSION_DIV) {
				result.append("\\frac{");
				result.append(b);
				result.append("}{");
				result.append(a);
				result.append("}");
			} else if (temp == StandardExtensions.EXTENSION_MUL) {
				result.append("(");
				result.append(b);
				result.append("\\cdot ");
				result.append(a);
				result.append(")");
			} else {
				result.append("(");
				result.append(b);
				result.append(temp.getName());
				result.append(a);
				result.append(")");
			}

		} else if (temp.getChildNodeCount() == 1) {
			String a = renderNode(node.getChildNode(0));
			result.append("(");
			result.append(temp.getName());
			result.append(a);
			result.append(")");
		} else {
			throw new EACompileError(
					"An operator must have an arity of 1 or 2, probably should be made a function.");
		}

		return result.toString();
	}

	public ExpressionNodeType determineNodeType(ProgramNode node) {
		if (node.getTemplate() == StandardExtensions.EXTENSION_CONST_SUPPORT) {
			return ExpressionNodeType.ConstVal;
		} else if (node.getTemplate() == StandardExtensions.EXTENSION_VAR_SUPPORT) {
			return ExpressionNodeType.Variable;
		} else if (node.getTemplate().getNodeType() == NodeType.OperatorLeft
				|| node.getTemplate().getNodeType() == NodeType.OperatorRight) {
			return ExpressionNodeType.Operator;
		} else {
			return ExpressionNodeType.Function;
		}
	}

	private String renderNode(ProgramNode node) {
		switch (determineNodeType(node)) {
		case ConstVal:
			return handleConst(node);
		case Operator:
			return handleOperator(node);
		case Variable:
			return handleVar(node);
		case Function:
			return handleFunction(node);
		}
		throw new EACompileError("Uknown node type: " + node.toString());
	}
}
