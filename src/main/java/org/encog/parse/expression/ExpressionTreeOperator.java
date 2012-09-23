package org.encog.parse.expression;

import org.encog.parse.expression.expvalue.ExpressionValue;

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
	public ExpressionValue evaluate() {
		if( name.equals("+") ) {
			return new ExpressionValue(this.argA.evaluate().getFloatValue() + this.argB.evaluate().getFloatValue());
		} else if( name.equals("-") ) {
			return new ExpressionValue(this.argA.evaluate().getFloatValue() - this.argB.evaluate().getFloatValue());
		} else if( name.equals("/") ) {
			return new ExpressionValue(this.argA.evaluate().getFloatValue() / this.argB.evaluate().getFloatValue());
		} else if( name.equals("*") ) {
			return new ExpressionValue(this.argA.evaluate().getFloatValue() * this.argB.evaluate().getFloatValue());
		} else if( name.equals("^") ) {
			return new ExpressionValue(Math.pow(this.argA.evaluate().toFloatValue(), this.argB.evaluate().toFloatValue()));
		} else {
			throw new ExpressionError("Unknown operator: " + name);
		}
		
	}
	
	public String toString() {
		return "[Opp: " + this.name + ", a:" + this.argA.toString() + ", b:" + this.argB.toString() + "]";
	}
	

}
