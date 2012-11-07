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

import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.FunctionFactory;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.parse.expression.common.ParseCommonExpression;

public class EncogProgram implements MLRegression {
		
	private EncogProgramVariables variables = new EncogProgramVariables();
	private EncogProgramContext context = new EncogProgramContext();

	public static ExpressionValue parse(final String str) {
		final EncogProgram holder = new EncogProgram(str);
		return holder.evaluate();
	}

	public static boolean parseBoolean(final String str) {
		final EncogProgram holder = new EncogProgram(str);
		return holder.evaluate().toBooleanValue();
	}

	public static ExpressionValue parseExpression(final String str) {
		final EncogProgram holder = new EncogProgram(str);
		return holder.evaluate();
	}

	public static double parseFloat(final String str) {
		final EncogProgram holder = new EncogProgram(str);
		return holder.evaluate().toFloatValue();
	}

	public static String parseString(final String str) {
		final EncogProgram holder = new EncogProgram(str);
		return holder.evaluate().toStringValue();
	}

	private ProgramNode rootNode;	

	public EncogProgram() {
		this(new EncogProgramContext(), new EncogProgramVariables());
		StandardExtensions.createAll(this.context.getFunctions());
		KnownConstTemplate.createAllConst(this.context.getFunctions());
	}
	
	public EncogProgram(EncogProgramContext theContext, EncogProgramVariables theVariables) {
		this.context = theContext;
		this.variables = theVariables;
	}

	public EncogProgram(final String expression) {
		this();
		compileExpression(expression);
	}

	public ProgramNode compileExpression(final String expression) {
		final ParseCommonExpression parser = new ParseCommonExpression(this);
		this.rootNode = parser.parse(expression);
		return this.rootNode;
	}

	public ExpressionValue evaluate() {
		return this.rootNode.evaluate();
	}
		
	public FunctionFactory getFunctions() {
		return this.context.getFunctions();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInputCount() {
		return this.variables.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getOutputCount() {
		return 1;
	}
	
	public ProgramNode getRootNode() {
		return rootNode;
	}
	
	public EncogProgramVariables getVariables() {
		return variables;
	}
	
	

	public EncogProgramContext getContext() {
		return context;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLData compute(MLData input) {
		if( input.size()!=getInputCount() ) {
			throw new ExpressionError("Invalid input count.");
		}
		
		for(int i=0;i<input.size();i++) {
			this.variables.getVariable(i).setValue(input.getData(i));
		}
		
		double d = this.rootNode.evaluate().toFloatValue();
		
		MLData result = new BasicMLData(1);
		result.setData(0, d);
		
		return result;
	}

}
