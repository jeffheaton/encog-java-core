package org.encog.parse.expression.extension;

import java.util.List;

import org.encog.parse.expression.ExpressionHolder;
import org.encog.parse.expression.ExpressionTreeElement;
import org.encog.parse.expression.ExpressionTreeFunction;

public interface ExpressionExtension {
	ExpressionTreeFunction factorFunction(ExpressionHolder theOwner, String theName, List<ExpressionTreeElement> theArgs);
}
