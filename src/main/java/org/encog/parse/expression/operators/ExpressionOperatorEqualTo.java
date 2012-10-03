package org.encog.parse.expression.operators;

import org.encog.Encog;
import org.encog.parse.expression.ExpressionTreeElement;
import org.encog.parse.expression.ExpressionTreeOperator;
import org.encog.parse.expression.expvalue.ExpressionValue;

public class ExpressionOperatorEqualTo extends ExpressionTreeOperator {

	public ExpressionOperatorEqualTo(ExpressionTreeElement argA,
			ExpressionTreeElement argB) {
		super("=", argA, argB);
	}
	
	@Override
	public ExpressionValue evaluate() {
		double diff = Math.abs(getArgA().evaluate().toFloatValue() - this.getArgB().evaluate().toFloatValue());
		return new ExpressionValue( diff<Encog.DEFAULT_DOUBLE_EQUAL);
	}
}
