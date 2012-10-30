package org.encog.ml.prg.extension;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.NodeFunction;
import org.encog.ml.prg.ProgramNode;
import org.encog.parse.expression.expvalue.EvaluateExpr;
import org.encog.parse.expression.expvalue.ExpressionValue;

public class StandardNumericOperators implements ProgramExtension {

	@Override
	public NodeFunction factorFunction(EncogProgram theOwner, String theName,
			ProgramNode[] theArgs) {
		
		// unary - operator
		if (theName.equals("-") && theArgs.length == 1) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(-this.getArgs().get(0).evaluate().toFloatValue());
				}
			};
		} if (theName.equals("+") && theArgs.length == 2) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return EvaluateExpr.add(getArgs().get(0).evaluate(), getArgs().get(1).evaluate());
				}
			};
		} if (theName.equals("-") && theArgs.length == 2) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return EvaluateExpr.sub(getArgs().get(0).evaluate(), getArgs().get(1).evaluate());
				}
			};
		} if (theName.equals("/") && theArgs.length == 2) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return EvaluateExpr.div(getArgs().get(0).evaluate(), getArgs().get(1).evaluate());
				}
			};
		} if (theName.equals("*") && theArgs.length == 2) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return EvaluateExpr.mul(getArgs().get(0).evaluate(), getArgs().get(1).evaluate());
				}
			};
		} if (theName.equals("^") && theArgs.length == 2) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return EvaluateExpr.pow(getArgs().get(0).evaluate(), getArgs().get(1).evaluate());
				}
			};
		} else {
			return null;
		}
	}

}
