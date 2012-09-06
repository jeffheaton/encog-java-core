package org.encog.parse.expression;

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
	public double evaluate() {
		if( name.equals("-") ) {
			return -this.argA.evaluate();
		} else {
			throw new ExpressionError("Unknown operator: " + name);
		}
		
	}
	
	public String toString() {
		return "[Opp: " + this.name + ", a:" + this.argA.toString() + ", b:" + "]";
	}
	
}
