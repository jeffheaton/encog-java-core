package org.encog.parse.expression.latex;

import org.encog.ml.prg.EncogProgram;
import org.encog.util.datastruct.StackString;

// "x=\\frac{-b \\pm \\sqrt {b^2-4ac}}{2a}";

public class RenderLatexExpression {
	private EncogProgram program;
	private StackString stack = new StackString(100);

	public RenderLatexExpression() {
	}

	public String render(final EncogProgram theProgram) {
		return null;
	}

}
