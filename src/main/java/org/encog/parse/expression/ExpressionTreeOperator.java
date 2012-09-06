package org.encog.parse.expression;

public class ExpressionTreeOperator extends ExpressionTreeElement {
	private final String name;
	private final ExpressionTreeElement argA;
	private final ExpressionTreeElement argB;
	
	public ExpressionTreeOperator(String name, ExpressionTreeElement argA,
			ExpressionTreeElement argB) {
		super();
		this.name = name;
		this.argA = argA;
		this.argB = argB;
	}

	public String getName() {
		return name;
	}

	public ExpressionTreeElement getArgA() {
		return argA;
	}

	public ExpressionTreeElement getArgB() {
		return argB;
	}

	@Override
	public double evaluate() {
		if( name.equals("+") ) {
			return this.argA.evaluate() + this.argB.evaluate();
		} else if( name.equals("-") ) {
			return this.argA.evaluate() - this.argB.evaluate();
		} else if( name.equals("/") ) {
			return this.argA.evaluate() / this.argB.evaluate();
		} else if( name.equals("*") ) {
			return this.argA.evaluate() * this.argB.evaluate();
		} else if( name.equals("^") ) {
			return Math.pow(this.argA.evaluate(), this.argB.evaluate());
		} else {
			throw new ExpressionError("Unknown operator: " + name);
		}
		
	}
	
	public String toString() {
		return "[Opp: " + this.name + ", a:" + this.argA.toString() + ", b:" + this.argB.toString() + "]";
	}
	

}
