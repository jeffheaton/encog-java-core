package org.encog.parse.expression;

import java.util.ArrayList;
import java.util.List;

import org.encog.parse.expression.expvalue.ExpressionValue;

public abstract class ExpressionTreeFunction extends ExpressionTreeElement {

	private final String name;
	private final List<ExpressionTreeElement> args = new ArrayList<ExpressionTreeElement>();
	private final ExpressionHolder owner;
	
	public ExpressionTreeFunction(ExpressionHolder theOwner, String theName, List<ExpressionTreeElement> theArgs) {
		this.owner = theOwner;
		this.name = theName;
		this.args.addAll(theArgs);
	}
	
	
	
	public String getName() {
		return name;
	}



	public List<ExpressionTreeElement> getArgs() {
		return args;
	}



	public ExpressionHolder getOwner() {
		return owner;
	}
}
