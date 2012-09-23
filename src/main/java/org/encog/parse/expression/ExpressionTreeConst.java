package org.encog.parse.expression;

import org.encog.parse.expression.expvalue.ExpressionValue;

public class ExpressionTreeConst extends ExpressionTreeElement {
	
	private final ExpressionValue value;

	public ExpressionTreeConst(ExpressionValue value) {
		super();
		this.value = value;
	}

	@Override
	public ExpressionValue evaluate() {
		return this.value;
	}
	
	public String toString() {
		return "[Const: " + this.toString() + "]";
	}
	
	
}
