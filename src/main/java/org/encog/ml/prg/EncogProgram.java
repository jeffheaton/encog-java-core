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

import java.util.List;
import java.util.Random;

import org.encog.EncogError;
import org.encog.ml.MLError;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.ea.exception.EACompileError;
import org.encog.ml.ea.exception.EARuntimeError;
import org.encog.ml.ea.genome.BasicGenome;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.expvalue.ValueType;
import org.encog.ml.prg.extension.FunctionFactory;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.prg.train.PrgPopulation;
import org.encog.ml.tree.traverse.tasks.TaskGetNodeIndex;
import org.encog.ml.tree.traverse.tasks.TaskReplaceNode;
import org.encog.parse.expression.common.ParseCommonExpression;
import org.encog.parse.expression.common.RenderCommonExpression;
import org.encog.parse.expression.epl.ParseEPL;
import org.encog.parse.expression.epl.RenderEPL;
import org.encog.parse.expression.rpn.RenderRPN;
import org.encog.util.simple.EncogUtility;

public class EncogProgram extends BasicGenome implements MLRegression, MLError {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;
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
		StandardExtensions.createAll(this.context);
	}

	public EncogProgram(EncogProgramContext theContext) {
		this(theContext, new EncogProgramVariables());
	}

	public EncogProgram(EncogProgramContext theContext,
			EncogProgramVariables theVariables) {
		this.context = theContext;
		this.variables = theVariables;

		// define variables
		for (VariableMapping v : this.context.getDefinedVariables()) {
			this.variables.defineVariable(v);
		}
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
		if (input.size() != getInputCount()) {
			throw new EACompileError("Invalid input count.");
		}

		for (int i = 0; i < input.size(); i++) {
			this.variables.setVariable(i, input.getData(i));
		}

		ExpressionValue v = this.rootNode.evaluate();
		VariableMapping resultMapping = getResultType();

		MLData result = new BasicMLData(1);
		boolean success = false;
		
		switch (resultMapping.getVariableType()) {
			case floatingType:
				if( v.isNumeric() ) {
					result.setData(0, v.toFloatValue());
					success = true;
				}
				break;
			case stringType:
				result.setData(0,v.toFloatValue());
				success = true;
				break;
			case booleanType:
				if( v.isBoolean() ) {
					result.setData(0,v.toBooleanValue()?1.0:0.0);
					success = true;
				}
				break;
			case intType:
				if( v.isNumeric() ) { 
					result.setData(0,v.toIntValue());
					success = true;
				}
				break;
			case enumType:
				if( v.isEnum() ) {
					result.setData(0,v.toIntValue());
					success = true;
				}
				break;
		}
		
		if(!success) {
			throw new EARuntimeError("EncogProgram produced " + v.getCurrentType().toString() + " but " + resultMapping.getVariableType().toString() + " was expected.");
		}

		return result;
	}

	private VariableMapping getResultType() {
		return ((PrgPopulation) getPopulation()).getContext().getResult();
	}

	public void setRootNode(ProgramNode theRootNode) {
		this.rootNode = theRootNode;
	}

	public ProgramNode findNode(int index) {
		return (ProgramNode) TaskGetNodeIndex.process(index, this.rootNode);
	}

	public void replaceNode(ProgramNode replaceThisNode, ProgramNode replaceWith) {
		if (replaceThisNode == this.rootNode) {
			this.rootNode = replaceWith;
		} else {
			TaskReplaceNode
					.process(this.rootNode, replaceThisNode, replaceWith);
		}
	}

	@Override
	public String toString() {
		RenderRPN render = new RenderRPN();
		String code = render.render(this);
		StringBuilder result = new StringBuilder();
		result.append("[EncogProgram: size=");
		result.append(size());
		result.append(", score=");
		result.append(this.getScore());
		result.append(",code=");
		result.append(code);
		result.append("]");
		return result.toString();
	}

	@Override
	public int size() {
		return this.rootNode.size();
	}

	@Override
	public double calculateError(MLDataSet data) {
		return EncogUtility.calculateRegressionError(this, data);
	}

	@Override
	public void copy(Genome source) {
		// TODO Auto-generated method stub

	}

	public String dumpAsCommonExpression() {
		RenderCommonExpression render = new RenderCommonExpression();
		return render.render(this);
	}

	public ProgramNode compileEPL(String code) {
		final ParseEPL parser = new ParseEPL(this);
		this.rootNode = parser.parse(code);
		return this.rootNode;
	}

	public String generateEPL() {
		RenderEPL render = new RenderEPL();
		return render.render(this);
	}

	public ValueType getReturnType() {
		return ((PrgPopulation) this.getPopulation()).getContext().getResult()
				.getVariableType();
	}

	public int selectRandomVariable(Random rnd, ValueType desiredType) {
		List<VariableMapping> selectionSet = this.context.findVariablesByType(desiredType);
		if( selectionSet.size()==0 && desiredType==ValueType.intType ) {
			selectionSet = this.context.findVariablesByType(ValueType.floatingType);	
		}
		
		if( selectionSet.size()==0) {
			return -1;
		}
		
		VariableMapping selected = selectionSet.get(rnd.nextInt(selectionSet.size()));
		return this.getContext().getDefinedVariables().indexOf(selected);
	}
}
