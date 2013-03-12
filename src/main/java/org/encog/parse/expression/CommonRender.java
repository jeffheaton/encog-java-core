package org.encog.parse.expression;

import org.encog.ml.prg.ProgramNode;

public class CommonRender {
	public ExpressionNodeType determineNodeType(ProgramNode node) {

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
}
