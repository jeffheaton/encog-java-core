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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.ml.ea.exception.EARuntimeError;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.expvalue.ValueType;
import org.encog.ml.prg.extension.FunctionFactory;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.util.csv.CSVFormat;

/**
 * Every EncogProgram must belong to a context. When programs are in a
 * population, they must all share a common context. The context defines
 * attributes that are common to all programs. The following information is
 * stored in a context.
 * 
 * The number formatting used. Namely, what type of radix point should strings
 * be parsed/rendered to.
 * 
 * The functions, or opcodes, that are available to the program. This defines
 * the set of functions and operators that a program might use. For an Encog
 * Program all operators are treated as functions internally. A operator is
 * essentially a shortcut notation for common functions.
 * 
 * The defined variables. These variables are constant for the run of the
 * program, but typically change for each run. They are essentially the
 * variables that make up an algebraic expression.
 * 
 * Finally, the return value mapping for the programs.
 */
public class EncogProgramContext implements Serializable {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The number formatting used. Namely, what type of radix point should
	 * strings be parsed/rendered to.
	 */
	private final CSVFormat format;

	/**
	 * The functions, or opcodes, that are available to the program. This
	 * defines the set of functions and operators that a program might use. For an
	 * Encog Program all operators are treated as functions internally. A
	 * operator is essentially a shortcut notation for common functions.
	 */
	private final FunctionFactory functions;

	/**
	 * The defined variables. These variables are constant for the run of the
	 * program, but typically change for each run. They are essentially the
	 * variables that make up an algebraic expression.
	 */
	private final List<VariableMapping> definedVariables = new ArrayList<VariableMapping>();

	/**
	 * Lookup map for the defined variables.
	 */
	private final Map<String, VariableMapping> map = new HashMap<String, VariableMapping>();

	/**
	 * The return value mapping for the programs.
	 */
	private VariableMapping result = new VariableMapping(null,
			ValueType.floatingType);

	/**
	 * Construct the context with an English number format and an empty function
	 * factory.
	 */
	public EncogProgramContext() {
		this(CSVFormat.ENGLISH, new FunctionFactory());
	}

	/**
	 * Construct a context with the specified number format and an empty
	 * function factory.
	 * 
	 * @param format
	 *            The format.
	 */
	public EncogProgramContext(final CSVFormat format) {
		this(format, new FunctionFactory());
	}

	/**
	 * Construct the context with the specified format and function factory.
	 * 
	 * @param theFormat
	 *            The format.
	 * @param theFunctions
	 *            The function factory.
	 */
	public EncogProgramContext(final CSVFormat theFormat,
			final FunctionFactory theFunctions) {
		this.format = theFormat;
		this.functions = theFunctions;
	}

	/**
	 * Clear the defined variables.
	 */
	public void clearDefinedVariables() {
		this.definedVariables.clear();
		this.map.clear();
	}

	/**
	 * Clone a branch of the program from the specified node.
	 * 
	 * @param targetProgram
	 *            The program that this branch will be "grafted" into.
	 * @param sourceBranch
	 *            The branch to clone, from the source program.
	 * @return The cloned branch.
	 */
	public ProgramNode cloneBranch(final EncogProgram targetProgram,
			final ProgramNode sourceBranch) {
		if (sourceBranch == null) {
			throw new EncogError("Can't clone null branch.");
		}

		final String name = sourceBranch.getName();

		// create any subnodes
		final ProgramNode[] args = new ProgramNode[sourceBranch.getChildNodes()
				.size()];
		for (int i = 0; i < args.length; i++) {
			args[i] = cloneBranch(targetProgram, (ProgramNode) sourceBranch
					.getChildNodes().get(i));
		}

		final ProgramNode result = targetProgram.getContext().getFunctions()
				.factorProgramNode(name, targetProgram, args);

		// now copy the expression data for the node
		for (int i = 0; i < sourceBranch.getData().length; i++) {
			result.getData()[i] = new ExpressionValue(sourceBranch.getData()[i]);
		}

		// return the new node
		return result;
	}

	/**
	 * Clone an entire program, keep the same context.
	 * 
	 * @param sourceProgram
	 *            The source program.
	 * @return The cloned program.
	 */
	public EncogProgram cloneProgram(final EncogProgram sourceProgram) {
		final ProgramNode rootNode = sourceProgram.getRootNode();
		final EncogProgram result = new EncogProgram(this);
		result.setRootNode(cloneBranch(result, rootNode));
		return result;
	}

	/**
	 * Create a new program, using this context.
	 * 
	 * @param expression
	 *            The common expression to compile.
	 * @return The resulting program.
	 */
	public EncogProgram createProgram(final String expression) {
		final EncogProgram result = new EncogProgram(this);
		result.compileExpression(expression);
		return result;
	}

	/**
	 * Define the specified variable as floating point.
	 * 
	 * @param theName
	 *            The variable name to define.
	 */
	public void defineVariable(final String theName) {
		defineVariable(theName, ValueType.floatingType, 0, 0);
	}

	/**
	 * Define the specified variable as the specified type. Don't use this for
	 * enums.
	 * 
	 * @param theName
	 *            The name of the variable.
	 * @param theVariableType
	 *            The variable type.
	 */
	public void defineVariable(final String theName,
			final ValueType theVariableType) {
		defineVariable(theName, theVariableType, 0, 0);
	}

	/**
	 * Define a variable.
	 * 
	 * @param theName
	 *            The name of the variable.
	 * @param theVariableType
	 *            The type of variable.
	 * @param theEnumType
	 *            The enum type, not used if not an enum type.
	 * @param theEnumValueCount
	 *            The number of values for the enum, not used if not an enum
	 *            type.
	 */
	public void defineVariable(final String theName,
			final ValueType theVariableType, final int theEnumType,
			final int theEnumValueCount) {
		final VariableMapping mapping = new VariableMapping(theName,
				theVariableType, theEnumType, theEnumValueCount);
		defineVariable(mapping);

	}

	/**
	 * Define a variable, based on a mapping.
	 * 
	 * @param mapping
	 *            The variable mapping.
	 */
	public void defineVariable(final VariableMapping mapping) {
		if (this.map.containsKey(mapping.getName())) {
			throw new EARuntimeError("Variable " + mapping.getName()
					+ " already defined.");
		}
		this.map.put(mapping.getName(), mapping);
		this.definedVariables.add(mapping);
	}

	/**
	 * Find all of the variables of the specified types.
	 * 
	 * @param desiredTypes
	 *            The types to look for.
	 * @return The variables that matched the specified types.
	 */
	public List<VariableMapping> findVariablesByTypes(
			final List<ValueType> desiredTypes) {
		final List<VariableMapping> result = new ArrayList<VariableMapping>();

		for (final VariableMapping mapping : this.definedVariables) {
			if (desiredTypes.contains(mapping.getVariableType())) {
				result.add(mapping);
			}
		}

		return result;
	}

	/**
	 * @return The defined variables.
	 */
	public List<VariableMapping> getDefinedVariables() {
		return this.definedVariables;
	}

	/**
	 * Get the enum ordinal count for the specified enumeration type.
	 * 
	 * @param enumType
	 *            The enumeration type.
	 * @return The ordinal count for the specified enumeration type.
	 */
	public int getEnumCount(final int enumType) {

		// make sure we consider the result
		if (this.result.getVariableType() == ValueType.enumType
				&& this.result.getEnumType() == enumType) {
			return this.result.getEnumValueCount();
		}

		for (final VariableMapping mapping : this.definedVariables) {
			if (mapping.getVariableType() == ValueType.enumType) {
				if (mapping.getEnumType() == enumType) {
					return mapping.getEnumValueCount();
				}
			}
		}
		throw new EARuntimeError("Undefined enum type: " + enumType);
	}

	/**
	 * @return The number formatting used. Namely, what type of radix point
	 *         should strings be parsed/rendered to.
	 */
	public CSVFormat getFormat() {
		return this.format;
	}

	/**
	 * @return The functions, or opcodes, that are available to the program.
	 *         This defines the set of functions and operators that a program
	 *         might use. For an Encog Program all operators are treated as
	 *         functions internally. A operator is essentially a shortcut
	 *         notation for common functions.
	 */
	public FunctionFactory getFunctions() {
		return this.functions;
	}

	/**
	 * Get the max enum type for all defined variables.
	 * 
	 * @return The max enumeration type.
	 */
	public int getMaxEnumType() {
		int r = -1;

		// make sure we consider the result
		if (this.result.getVariableType() == ValueType.enumType) {
			r = this.result.getEnumType();
		}

		// loop over all mappings and find the max enum type
		for (final VariableMapping mapping : this.definedVariables) {
			if (mapping.getVariableType() == ValueType.enumType) {
				r = Math.max(r, mapping.getEnumType());
			}
		}

		// if we did not find one then there are no enum types
		if (r == -1) {
			throw new EARuntimeError("No enum types defined in context.");
		}

		return r;
	}

	/**
	 * @return the result
	 */
	public VariableMapping getResult() {
		return this.result;
	}

	/**
	 * @return True, if enums are defined.
	 */
	public boolean hasEnum() {
		if (this.result.getVariableType() == ValueType.enumType) {
			return true;
		}

		for (final VariableMapping mapping : this.definedVariables) {
			if (mapping.getVariableType() == ValueType.enumType) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Load all known functions as opcodes.
	 */
	public void loadAllFunctions() {
		StandardExtensions.createAll(this);
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(final VariableMapping result) {
		this.result = result;
	}

}
