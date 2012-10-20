/*
 * Encog(tm) Core v3.2 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.parse.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.app.analyst.csv.process.ProcessExtension;
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

	public static boolean parseBoolean(String str) {
		ExpressionHolder holder = new ExpressionHolder(str);
		return holder.evaluate(0).toBooleanValue();
	}

	
	
}
