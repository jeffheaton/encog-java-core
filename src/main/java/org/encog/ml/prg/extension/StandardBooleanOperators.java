package org.encog.ml.prg.extension;

import org.encog.Encog;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.NodeFunction;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ExpressionValue;

public class StandardBooleanOperators implements ProgramExtension {

	@Override
	public NodeFunction factorFunction(EncogProgram theOwner, String theName,
			ProgramNode[] theArgs) {
		
		if (theName.equals("&") && theArgs.length == 2) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(getArgs().get(0).evaluate().toBooleanValue() && getArgs().get(1).evaluate().toBooleanValue());
				}
			};
		} if (theName.equals("|") && theArgs.length == 2) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(getArgs().get(0).evaluate().toBooleanValue() || getArgs().get(1).evaluate().toBooleanValue());
				}
			};
		} if (theName.equals("=") && theArgs.length == 2) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					double diff = Math.abs(getArgs().get(0).evaluate().toFloatValue() - getArgs().get(1).evaluate().toFloatValue());
					return new ExpressionValue( diff<Encog.DEFAULT_DOUBLE_EQUAL);
				}
			};
		} if (theName.equals(">") && theArgs.length == 2) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(getArgs().get(0).evaluate().toFloatValue() > getArgs().get(1).evaluate().toFloatValue());
				}
			};
		} if (theName.equals("<") && theArgs.length == 2) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(getArgs().get(0).evaluate().toFloatValue() < getArgs().get(1).evaluate().toFloatValue());
				}
			};
		} if (theName.equals(">=") && theArgs.length == 2) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(getArgs().get(0).evaluate().toFloatValue() >= getArgs().get(1).evaluate().toFloatValue());
				}
			};
		} if (theName.equals("<=") && theArgs.length == 2) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(getArgs().get(0).evaluate().toFloatValue() <= getArgs().get(1).evaluate().toFloatValue());
				}
			};
		} else {
			return null;
		}
	}

}
