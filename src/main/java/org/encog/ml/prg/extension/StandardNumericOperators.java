package org.encog.ml.prg.extension;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.NodeFunction;
import org.encog.ml.prg.ProgramNode;
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
		} else {
			return null;
		}
	}

}
