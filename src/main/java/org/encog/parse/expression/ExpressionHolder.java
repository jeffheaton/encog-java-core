package org.encog.parse.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.parse.expression.expvalue.ExpressionValue;

public class ExpressionHolder {

	private final List<ExpressionTreeElement> expressions = new ArrayList<ExpressionTreeElement>();
	private final Map<String,ExpressionValue> memory = new HashMap<String,ExpressionValue>();

	public ExpressionHolder(String expression) {
		compileExpression(expression);
	}

	public static ExpressionValue parse(String str) {
		ExpressionHolder holder = new ExpressionHolder(str);
		return holder.evaluate(0);
	}
	
	public ExpressionTreeElement compileExpression(String expression) {
		ExpressionParser parser = new ExpressionParser(this);
		ExpressionTreeElement e = parser.parse(expression);
		this.expressions.add(e);
		return e;
	}

	public ExpressionValue evaluate(int i) {
		return this.expressions.get(0).evaluate();
	}

	public void set(String name, ExpressionValue value) {
		this.memory.put(name, value);		
	}

	public boolean variableExists(String name) {
		return this.memory.containsKey(name);
	}

	public ExpressionValue get(String name) {
		return this.memory.get(name);
	}

	public static double parseFloat(String str) {
		ExpressionHolder holder = new ExpressionHolder(str);
		return holder.evaluate(0).toFloatValue();
	}

	public void set(String name, double d) {
		set(name,new ExpressionValue(d));
		
	}

	public static String parseString(String str) {
		ExpressionHolder holder = new ExpressionHolder(str);
		return holder.evaluate(0).toStringValue();
	}

	public List<ExpressionTreeElement> getExpressions() {
		return expressions;
	}
	
	
}
