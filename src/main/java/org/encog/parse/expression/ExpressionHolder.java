package org.encog.parse.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.app.analyst.csv.transform.ProcessExtension;
import org.encog.parse.expression.expvalue.ExpressionValue;
import org.encog.parse.expression.extension.ExpressionExtension;
import org.encog.parse.expression.extension.StandardFunctionsExtension;
import org.encog.util.csv.CSVFormat;

public class ExpressionHolder {

	private final List<ExpressionTreeElement> expressions = new ArrayList<ExpressionTreeElement>();
	private final Map<String,ExpressionValue> memory = new HashMap<String,ExpressionValue>();
	private final List<ExpressionExtension> extensions = new ArrayList<ExpressionExtension>();
	private CSVFormat format = CSVFormat.EG_FORMAT;

	public ExpressionHolder(String expression) {
		this();
		compileExpression(expression);
	}

	public ExpressionHolder() {
		this.extensions.add(new StandardFunctionsExtension());
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

	public void addExtension(ProcessExtension extension) {
		this.extensions.add(extension);
	}
	
	public void removeExtension(ProcessExtension extension) {
		this.extensions.remove(extension);
	}
	
	public List<ExpressionExtension> getExtensions() {
		return this.extensions;
	}

	public CSVFormat getFormat() {
		return format;
	}

	public void setFormat(CSVFormat format) {
		this.format = format;
	}

	public static ExpressionValue parseExpression(String str) {
		ExpressionHolder holder = new ExpressionHolder(str);
		return holder.evaluate(0);
	}

	
	
}
