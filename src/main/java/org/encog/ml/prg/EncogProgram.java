/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
import java.util.Random;

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

/**
 * Holds an Encog Programming Language (EPL) program. A Encog program is
 * internally represented as a tree structure. It can be rendered to a variety
 * of forms, such as RPN, common infix expressions, or Latex. The Encog
 * Workbench also allows display as a graphical tree. An Encog Program is both a
 * genome and phenome. No decoding is necessary.
 * 
 * Every Encog Program has a context. The context is the same for every Encog
 * Program in a population. The context defines which opcodes should be used, as
 * well as the defined variables.
 * 
 * The actual values for the variables are not stored in the context. Rather
 * they are stored in a variable holder. Each program usually has its own
 * variable holder, though it is possible to share.
 */
public class EncogProgram extends BasicGenome implements MLRegression, MLError {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Parse the specified program, or expression, and return the result. No
	 * variables can be defined for this as a default context is used. The
	 * result is returned as a boolean.
	 * 
	 * @param str
	 *            The program expression.
	 * @return The value the expression was evaluated to.
	 */
	public static boolean parseBoolean(final String str) {
		final EncogProgram holder = new EncogProgram(str);
		return holder.evaluate().toBooleanValue();
	}

	/**
	 * Parse the specified program, or expression, and return the result. No
	 * variables can be defined for this as a default context is used. The
	 * result is returned as a boolean.
	 * 
	 * @param str
	 *            The program expression.
	 * @return The value the expression was evaluated to.
	 */
	public static ExpressionValue parseExpression(final String str) {
		final EncogProgram holder = new EncogProgram(str);
		return holder.evaluate();
	}

	/**
	 * Parse the specified program, or expression, and return the result. No
	 * variables can be defined for this as a default context is used. The
	 * result is returned as a float.
	 * 
	 * @param str
	 *            The program expression value.
	 * @return The value the expression was evaluated to.
	 */
	public static double parseFloat(final String str) {
		final EncogProgram holder = new EncogProgram(str);
		return holder.evaluate().toFloatValue();
	}

	/**
	 * Parse the specified program, or expression, and return the result. No
	 * variables can be defined for this as a default context is used. The
	 * result is returned as a string.
	 * 
	 * @param str
	 *            The program expression value.
	 * @return The value the expression was evaluated to.
	 */
	public static String parseString(final String str) {
		final EncogProgram holder = new EncogProgram(str);
		return holder.evaluate().toStringValue();
	}

	/**
	 * The variables that will be used by this Encog program.
	 */
	private EncogProgramVariables variables = new EncogProgramVariables();

	/**
	 * The context that this Encog program executes in, the context is typically
	 * shared with other Encog programs.
	 */
	private EncogProgramContext context = new EncogProgramContext();

	/**
	 * The root node of the program.
	 */
	private ProgramNode rootNode;
	
	/**
	 * Holds extra data that might be needed by user extended opcodes.
	 */
	private Map<String,Object> extraData = new HashMap<String,Object>();

	/**
	 * Construct the Encog program and create a default context and variable
	 * holder. Use all available opcodes.
	 */
	public EncogProgram() {
		this(new EncogProgramContext(), new EncogProgramVariables());
		StandardExtensions.createAll(this.context);
	}

	/**
	 * Construct the Encog program with the specified context, but create a new
	 * variable holder.
	 * 
	 * @param theContext
	 *            The context.
	 */
	public EncogProgram(final EncogProgramContext theContext) {
		this(theContext, new EncogProgramVariables());
	}

	/**
	 * Construct an Encog program using the specified context and variable
	 * holder.
	 * 
	 * @param theContext
	 *            The context.
	 * @param theVariables
	 *            The variable holder.
	 */
	public EncogProgram(final EncogProgramContext theContext,
			final EncogProgramVariables theVariables) {
		this.context = theContext;
		this.variables = theVariables;

		// define variables
		for (final VariableMapping v : this.context.getDefinedVariables()) {
			this.variables.defineVariable(v);
		}
	}

	/**
	 * Construct an Encog program using the specified expression, but create an
	 * empty context and variable holder.
	 * 
	 * @param expression
	 *            The expression.
	 */
	public EncogProgram(final String expression) {
		this();
		compileExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateError(final MLDataSet data) {
		return EncogUtility.calculateRegressionError(this, data);
	}

	/**
	 * Compile the specified EPL into an actual program node structure, for
	 * later execution.
	 * 
	 * @param code The code to compile.
	 * @return The root node.
	 */
	public ProgramNode compileEPL(final String code) {
		final ParseEPL parser = new ParseEPL(this);
		this.rootNode = parser.parse(code);
		return this.rootNode;
	}

	/**
	 * Compile the specified expression.
	 * 
	 * @param expression
	 *            The expression.
	 * @return The program node that this was compiled into.
	 */
	public ProgramNode compileExpression(final String expression) {
		final ParseCommonExpression parser = new ParseCommonExpression(this);
		this.rootNode = parser.parse(expression);
		return this.rootNode;
	}

	/**
	 * Compute the output from the input MLData. The individual values of the
	 * input will be mapped to the variables defined in the context. The order
	 * is the same between the input and the defined variables. The input will
	 * be mapped to the appropriate types. Enums will use their ordinal number.
	 * The result will be a single number MLData.
	 * 
	 * @param input
	 *            The input to the program.
	 * @return A single numer MLData.
	 */
	@Override
	public MLData compute(final MLData input) {
		if (input.size() != getInputCount()) {
			throw new EACompileError("Invalid input count.");
		}

		for (int i = 0; i < input.size(); i++) {
			this.variables.setVariable(i, input.getData(i));
		}

		final ExpressionValue v = this.rootNode.evaluate();
		final VariableMapping resultMapping = getResultType();

		final MLData result = new BasicMLData(1);
		boolean success = false;

		switch (resultMapping.getVariableType()) {
		case floatingType:
			if (v.isNumeric()) {
				result.setData(0, v.toFloatValue());
				success = true;
			}
			break;
		case stringType:
			result.setData(0, v.toFloatValue());
			success = true;
			break;
		case booleanType:
			if (v.isBoolean()) {
				result.setData(0, v.toBooleanValue() ? 1.0 : 0.0);
				success = true;
			}
			break;
		case intType:
			if (v.isNumeric()) {
				result.setData(0, v.toIntValue());
				success = true;
			}
			break;
		case enumType:
			if (v.isEnum()) {
				result.setData(0, v.toIntValue());
				success = true;
			}
			break;
		}

		if (!success) {
			throw new EARuntimeError("EncogProgram produced "
					+ v.getExpressionType().toString() + " but "
					+ resultMapping.getVariableType().toString()
					+ " was expected.");
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void copy(final Genome source) {
		// not needed, already a genome
	}

	/**
	 * @return The string as a common "infix" expression.
	 */
	public String dumpAsCommonExpression() {
		final RenderCommonExpression render = new RenderCommonExpression();
		return render.render(this);
	}

	/**
	 * Execute the program and return the result.
	 * 
	 * @return The result of running the program.
	 */
	public ExpressionValue evaluate() {
		return this.rootNode.evaluate();
	}

	/**
	 * Find the specified node by index. The tree is traversed to do this. This
	 * is typically used to select a random node.
	 * 
	 * @param index
	 *            The index being sought.
	 * @return The program node found.
	 */
	public ProgramNode findNode(final int index) {
		return (ProgramNode) TaskGetNodeIndex.process(index, this.rootNode);
	}

	/**
	 * @return The string as an EPL expression. EPL is the format that
	 *         EncogPrograms are usually persisted as.
	 */
	public String generateEPL() {
		final RenderEPL render = new RenderEPL();
		return render.render(this);
	}

	/**
	 * @return The program context. The program context may be shared over
	 *         multiple programs.
	 */
	public EncogProgramContext getContext() {
		return this.context;
	}

	/**
	 * @return The function factory from the context.
	 */
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

	/**
	 * @return The variable mapping for the result type. This is obtained from
	 *         the context.
	 */
	private VariableMapping getResultType() {
		return ((PrgPopulation) getPopulation()).getContext().getResult();
	}

	/**
	 * @return The return type, from the context.
	 */
	public ValueType getReturnType() {
		return this.context.getResult().getVariableType();
	}

	/**
	 * @return The root node of the program.
	 */
	public ProgramNode getRootNode() {
		return this.rootNode;
	}

	/**
	 * @return The variable holder.
	 */
	public EncogProgramVariables getVariables() {
		return this.variables;
	}

	/**
	 * Replace the specified node with another.
	 * 
	 * @param replaceThisNode
	 *            The node to replace.
	 * @param replaceWith
	 *            The node that is replacing that node.
	 */
	public void replaceNode(final ProgramNode replaceThisNode,
			final ProgramNode replaceWith) {
		if (replaceThisNode == this.rootNode) {
			this.rootNode = replaceWith;
		} else {
			TaskReplaceNode
					.process(this.rootNode, replaceThisNode, replaceWith);
		}
	}

	/**
	 * Select a random variable from the defined variables.
	 * 
	 * @param rnd
	 *            A random number generator.
	 * @param desiredTypes
	 *            The desired types that the variable can be.
	 * @return The index of the defined variable, or -1 if unable to define.
	 */
	public int selectRandomVariable(final Random rnd,
			final List<ValueType> desiredTypes) {
		List<VariableMapping> selectionSet = this.context
				.findVariablesByTypes(desiredTypes);
		if (selectionSet.size() == 0
				&& desiredTypes.contains(ValueType.intType)) {
			final List<ValueType> floatList = new ArrayList<ValueType>();
			floatList.add(ValueType.floatingType);
			selectionSet = this.context.findVariablesByTypes(floatList);
		}

		if (selectionSet.size() == 0) {
			return -1;
		}

		final VariableMapping selected = selectionSet.get(rnd
				.nextInt(selectionSet.size()));
		return getContext().getDefinedVariables().indexOf(selected);
	}

	/**
	 * Set the root node for the program.
	 * 
	 * @param theRootNode
	 *            The new root node.
	 */
	public void setRootNode(final ProgramNode theRootNode) {
		this.rootNode = theRootNode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return this.rootNode.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final RenderRPN render = new RenderRPN();
		final String code = render.render(this);
		final StringBuilder result = new StringBuilder();
		result.append("[EncogProgram: size=");
		result.append(size());
		result.append(", score=");
		result.append(getScore());
		result.append(",code=");
		result.append(code);
		result.append("]");
		return result.toString();
	}

	/**
	 * Get extra data that might be needed by user extended opcodes.
	 * @param name The name the data was stored under.
	 * @return The extra data.
	 */
	public Object getExtraData(final String name) {
		return this.extraData.get(name);
	}
	
	/**
	 * Set extra data that might be needed by extensions.
	 * @param name The name of the data stored.
	 * @param value The data.
	 */
	public void setExtraData(final String name, final Object value) {
		this.extraData.put(name, value);
	}
}
