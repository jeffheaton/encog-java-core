package org.encog.parse.expression;

public class ExpressionTreeConst extends ExpressionTreeElement {
	
	private final double value;

	public ExpressionTreeConst(double value) {
		super();
		this.value = value;
	}

	@Override
	public double evaluate() {
		return this.value;
	}
	
	public String toString() {
		return "[Const: " + this.value + "]";
	}
	
	
}
