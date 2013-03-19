package org.encog.ml.prg.extension;

public enum NodeType {
	OperatorLeft,
	OperatorRight,
	Leaf,
	Function,
	Unary, 
	None;
	
	public boolean isOperator() {
		return( this==OperatorLeft || this==OperatorRight || this==Unary );
	}
}
