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
import java.util.Map.Entry;

import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.FunctionFactory;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.parse.expression.ExpressionError;
import org.encog.parse.expression.ExpressionParser;
import org.encog.util.csv.CSVFormat;

public class EncogProgram {
	
	private FunctionFactory functions = new FunctionFactory(this);

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

	private final Map<String, Integer> varMap = new HashMap<String, Integer>();
	
	private final List<ExpressionValue> variables = new ArrayList<ExpressionValue>();

	private CSVFormat format = CSVFormat.EG_FORMAT;

	public EncogProgram() {
		StandardExtensions.createAll(this.functions);
	}

	public EncogProgram(final String expression) {
		this();
		compileExpression(expression);
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

	public ExpressionValue getVariable(final String name) {
		if( this.varMap.containsKey(name)) {
			int index = this.varMap.get(name);
			return this.variables.get(index);
		} else {
			return null;
		}
	}

	public List<ProgramNode> getExpressions() {
		return this.expressions;
	}

	public CSVFormat getFormat() {
		return this.format;
	}

	public void setVariable(final String name, final double d) {
		setVariable(name, new ExpressionValue(d));

	}

	public synchronized void setVariable(final String name, final ExpressionValue value) {
		if( this.varMap.containsKey(name)) {
			int index = this.varMap.get(name);
			this.variables.set(index, value);
		} else {
			this.varMap.put(name, this.variables.size());
			this.variables.add(value);
		}
	}

	public void setFormat(final CSVFormat format) {
		this.format = format;
	}

	public boolean variableExists(final String name) {
		return this.varMap.containsKey(name);
	}
	
	public FunctionFactory getFunctions() {
		return this.functions;
	}

	public ExpressionValue getVariable(int i) {
		return this.variables.get(i);
	}

	public int getVariableIndex(String varName) {
		if( !variableExists(varName) ) {
			throw new ExpressionError("Undefined variable: " + varName);
		}
		
		return this.varMap.get(varName);
	}

	public String getVariableName(int idx) {
		for( Entry<String, Integer> entry: this.varMap.entrySet()) {
			if( entry.getValue()==idx) {
				return entry.getKey();
			}
		}
		
		return null;
	}

}
