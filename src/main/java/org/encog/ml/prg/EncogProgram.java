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
package org.encog.ml.prg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.app.analyst.csv.process.ProcessExtension;
import org.encog.parse.expression.ExpressionParser;
import org.encog.parse.expression.expvalue.ExpressionValue;
import org.encog.parse.expression.extension.ExpressionExtension;
import org.encog.parse.expression.extension.StandardFunctionsExtension;
import org.encog.util.csv.CSVFormat;

public class EncogProgram {

	public static ExpressionValue parse(final String str) {
		final EncogProgram holder = new EncogProgram(str);
		return holder.evaluate(0);
	}

	public static boolean parseBoolean(final String str) {
		final EncogProgram holder = new EncogProgram(str);
		return holder.evaluate(0).toBooleanValue();
	}

	public static ExpressionValue parseExpression(final String str) {
		final EncogProgram holder = new EncogProgram(str);
		return holder.evaluate(0);
	}

	public static double parseFloat(final String str) {
		final EncogProgram holder = new EncogProgram(str);
		return holder.evaluate(0).toFloatValue();
	}

	public static String parseString(final String str) {
		final EncogProgram holder = new EncogProgram(str);
		return holder.evaluate(0).toStringValue();
	}

	private final List<ProgramNode> expressions = new ArrayList<ProgramNode>();

	private final Map<String, ExpressionValue> memory = new HashMap<String, ExpressionValue>();

	private final List<ExpressionExtension> extensions = new ArrayList<ExpressionExtension>();

	private CSVFormat format = CSVFormat.EG_FORMAT;

	public EncogProgram() {
		this.extensions.add(new StandardFunctionsExtension());
	}

	public EncogProgram(final String expression) {
		this();
		compileExpression(expression);
	}

	public void addExtension(final ProcessExtension extension) {
		this.extensions.add(extension);
	}

	public ProgramNode compileExpression(final String expression) {
		final ExpressionParser parser = new ExpressionParser(this);
		final ProgramNode e = parser.parse(expression);
		this.expressions.add(e);
		return e;
	}

	public ExpressionValue evaluate(final int i) {
		return this.expressions.get(0).evaluate();
	}

	public ExpressionValue get(final String name) {
		return this.memory.get(name);
	}

	public List<ProgramNode> getExpressions() {
		return this.expressions;
	}

	public List<ExpressionExtension> getExtensions() {
		return this.extensions;
	}

	public CSVFormat getFormat() {
		return this.format;
	}

	public void removeExtension(final ProcessExtension extension) {
		this.extensions.remove(extension);
	}

	public void set(final String name, final double d) {
		set(name, new ExpressionValue(d));

	}

	public void set(final String name, final ExpressionValue value) {
		this.memory.put(name, value);
	}

	public void setFormat(final CSVFormat format) {
		this.format = format;
	}

	public boolean variableExists(final String name) {
		return this.memory.containsKey(name);
	}

}
