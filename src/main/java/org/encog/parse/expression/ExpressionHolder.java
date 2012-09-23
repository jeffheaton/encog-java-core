package org.encog.parse.expression;

import java.util.HashMap;
import java.util.Map;

import org.encog.parse.expression.expvalue.ExpressionValue;

public class ExpressionHolder {

	private final ExpressionTreeElement root;
	private final Map<String,ExpressionValue> memory = new HashMap<String,ExpressionValue>();

	public ExpressionHolder(String expression) {
		ExpressionParser parser = new ExpressionParser(this);
		this.root = parser.parse(expression);
	}

	public static ExpressionValue parse(String str) {
		ExpressionHolder holder = new ExpressionHolder(str);
		return holder.evaluate();
	}

	public ExpressionValue evaluate() {
		return root.evaluate();
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
		return holder.evaluate().getFloatValue();
	}

	public void set(String name, double d) {
		set(name,new ExpressionValue(d));
		
	}
}
