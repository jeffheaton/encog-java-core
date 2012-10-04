package org.encog.parse.expression.operators;

import org.encog.parse.expression.ExpressionTreeElement;
import org.encog.parse.expression.ExpressionTreeOperator;
import org.encog.parse.expression.expvalue.ExpressionValue;

public class ExpressionOperatorGreaterThanEqual extends ExpressionTreeOperator {

	public ExpressionOperatorGreaterThanEqual(ExpressionTreeElement argA,
			ExpressionTreeElement argB) {
		super(">=", argA, argB);
	}
	
	@Override
	public ExpressionValue evaluate() {
		return new ExpressionValue(getArgA().evaluate().toFloatValue() >= this.getArgB().evaluate().toFloatValue());
	}
}
