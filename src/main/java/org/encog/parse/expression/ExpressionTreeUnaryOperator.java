package org.encog.parse.expression;

import org.encog.parse.expression.expvalue.ExpressionValue;

public class ExpressionTreeUnaryOperator extends ExpressionTreeElement {
	private final String name;
	private final ExpressionTreeElement argA;
	
	public ExpressionTreeUnaryOperator(String name, ExpressionTreeElement argA) {
		super();
		this.name = name;
		this.argA = argA;
	}

	public String getName() {
		return name;
	}

	public ExpressionTreeElement getArgA() {
		return argA;
	}

	@Override
	public ExpressionValue evaluate() {
		if( name.equals("-") ) {
			return new ExpressionValue(-this.argA.evaluate().toFloatValue());
		} else {
			throw new ExpressionError("Unknown operator: " + name);
		}
		
	}
	
	public String toString() {
		return "[Opp: " + this.name + ", a:" + this.argA.toString() + ", b:" + "]";
	}
	
}
