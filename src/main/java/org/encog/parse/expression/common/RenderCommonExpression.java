package org.encog.parse.expression.common;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.KnownConstNode;
import org.encog.ml.prg.ProgramNode;
import org.encog.parse.expression.ExpressionNodeType;

public class RenderCommonExpression {
	private EncogProgram holder;

	public RenderCommonExpression() {
	}

	public String render(final EncogProgram theHolder) {
		this.holder = theHolder;
		ProgramNode node = theHolder.getRootNode();
		return renderNode(node);
	}

	private String renderConst(ProgramNode node) {
		return node.getExpressionData()[0].toStringValue();
	}
	
	private String renderConstKnown(KnownConstNode node) {
		return node.getName();
	}

	private String renderVar(ProgramNode node) {
		int varIndex = node.getIntData()[0];
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

	public ExpressionNodeType determineNodeType(ProgramNode node) {
		
		if( node instanceof KnownConstNode) {
			return ExpressionNodeType.ConstKnown;
		}
		
		if (node.getName().equals("#const")) {
			return ExpressionNodeType.ConstVal;
		}  
			
		if (node.getName().equals("#var")) {
			return ExpressionNodeType.Variable;
		} 
		
		if( node.getChildNodes().size()!=2 ) {
			return ExpressionNodeType.Function;
		}
		
		String name = node.getName();
		
		if( !Character.isLetterOrDigit(name.charAt(0)) ) {
			return ExpressionNodeType.Operator;			
		}
		
		return ExpressionNodeType.Function;		
	}

	private String renderNode(ProgramNode node) {
		StringBuilder result = new StringBuilder();

		switch (determineNodeType(node)) {
		case ConstVal:
			result.append(renderConst(node));
			break;
		case ConstKnown:
			result.append(renderConstKnown((KnownConstNode)node));
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
