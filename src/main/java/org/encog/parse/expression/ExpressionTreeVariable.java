package org.encog.parse.expression;

import org.encog.parse.expression.expvalue.ExpressionValue;

public class ExpressionTreeVariable extends ExpressionTreeElement {
	final private ExpressionHolder owner;
	final private String name;
	
	public ExpressionTreeVariable(ExpressionHolder theOwner, String theName) {
		this.owner = theOwner;
		this.name = theName;
	}

	@Override
	public ExpressionValue evaluate() {
		if( !owner.variableExists(this.name) ) {
			throw new ExpressionError("Undefined variable: " + this.name);
		}

		return this.owner.get(this.name);
	}
	
	
}
