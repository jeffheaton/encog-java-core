package org.encog.parse.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.util.SimpleParser;

public class ExpressionHolder {

	private final ExpressionTreeElement root;
	private final Map<String,Double> memory = new HashMap<String,Double>();

	public ExpressionHolder(String expression) {
		ExpressionParser parser = new ExpressionParser(this);
		this.root = parser.parse(expression);
	}

	public static double parse(String str) {
		ExpressionHolder holder = new ExpressionHolder(str);
		return holder.evaluate();
	}

	public double evaluate() {
		return root.evaluate();
	}

	public void set(String name, double value) {
		this.memory.put(name, value);		
	}

	public boolean variableExists(String name) {
		return this.memory.containsKey(name);
	}

	public double get(String name) {
		return this.memory.get(name);
	}
}
