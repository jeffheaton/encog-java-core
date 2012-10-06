package org.encog.parse.expression.operators;

import org.encog.parse.expression.ExpressionTreeElement;
import org.encog.parse.expression.ExpressionTreeOperator;
import org.encog.parse.expression.expvalue.EvaluateExpr;
import org.encog.parse.expression.expvalue.ExpressionValue;

public class ExpressionOperatorAdd extends ExpressionTreeOperator {

	public ExpressionOperatorAdd(ExpressionTreeElement argA,
			ExpressionTreeElement argB) {
		super("+", argA, argB);
	}
	
	@Override
	public ExpressionValue evaluate() {
		return EvaluateExpr.add(getArgA().evaluate(), this.getArgB().evaluate());
	}
}
