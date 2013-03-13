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
