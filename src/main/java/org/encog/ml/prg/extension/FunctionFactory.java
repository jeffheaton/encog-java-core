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
package org.encog.ml.prg.extension;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.ml.ea.exception.EACompileError;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ValueType;

/**
 * A function factory maps the opcodes contained in the EncogOpcodeRegistry into
 * an EncogProgram. You rarely want to train with every available opcode. For
 * example, if you are only looking to produce an equation, you should not make
 * use of the if-statement and logical operators.
 */
public class FunctionFactory implements Serializable {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * A map for quick lookup.
	 */
	private final Map<String, ProgramExtensionTemplate> templateMap = new HashMap<String, ProgramExtensionTemplate>();

	/**
	 * The opcodes.
	 */
	private final List<ProgramExtensionTemplate> opcodes = new ArrayList<ProgramExtensionTemplate>();

	/**
	 * Default constructor.
	 */
	public FunctionFactory() {
	}

	/**
	 * Add an opcode to the function factory. The opcode must exist in the
	 * opcode registry.
	 * 
	 * @param ext
	 *            The opcode to add.
	 */
	public void addExtension(final ProgramExtensionTemplate ext) {
		addExtension(ext.getName(), ext.getChildNodeCount());
	}

	/**
	 * Add an opcode to the function factory from the opcode registry.
	 * 
	 * @param name
	 *            The name of the opcode.
	 * @param args
	 *            The number of arguments.
	 */
	public void addExtension(final String name, final int args) {
		final String key = EncogOpcodeRegistry.createKey(name, args);
		if (!this.templateMap.containsKey(key)) {
			final ProgramExtensionTemplate temp = EncogOpcodeRegistry.INSTANCE
					.findOpcode(name, args);
			if (temp == null) {
				throw new EACompileError("Unknown extension " + name + " with "
						+ args + " arguments.");
			}
			this.opcodes.add(temp);
			this.templateMap.put(key, temp);
		}

	}

	/**
	 * Factor a new program node, based in a template object.
	 * 
	 * @param temp
	 *            The opcode.
	 * @param program
	 *            The program.
	 * @param args
	 *            The arguments for this node.
	 * @return The newly created ProgramNode.
	 */
	public ProgramNode factorProgramNode(final ProgramExtensionTemplate temp,
			final EncogProgram program, final ProgramNode[] args) {
		return new ProgramNode(program, temp, args);
	}

	/**
	 * Factor a new program node, based on an opcode name and arguments.
	 * 
	 * @param name
	 *            The name of the opcode.
	 * @param program
	 *            The program to factor for.
	 * @param args
	 *            The arguements.
	 * @return The newly created ProgramNode.
	 */
	public ProgramNode factorProgramNode(final String name,
			final EncogProgram program, final ProgramNode[] args) {

		final String key = EncogOpcodeRegistry.createKey(name, args.length);

		if (!this.templateMap.containsKey(key)) {
			throw new EACompileError("Undefined function/operator: " + name
					+ " with " + args.length + " args.");
		}

		final ProgramExtensionTemplate temp = this.templateMap.get(key);
		return new ProgramNode(program, temp, args);
	}

	/**
	 * Find a function with the specified name.
	 * 
	 * @param name
	 *            The name of the function.
	 * @return The function opcode.
	 */
	public ProgramExtensionTemplate findFunction(final String name) {
		for (final ProgramExtensionTemplate opcode : this.opcodes) {
			// only consider functions
			if (opcode.getNodeType() == NodeType.Function) {
				if (opcode.getName().equals(name)) {
					return opcode;
				}
			}
		}
		return null;
	}

	/**
	 * Find all opcodes that match the search criteria.
	 * 
	 * @param types
	 *            The return types to consider.
	 * @param context
	 *            The program context.
	 * @param includeTerminal
	 *            True, to include the terminals.
	 * @param includeFunction
	 *            True, to include the functions.
	 * @return The opcodes found.
	 */
	public List<ProgramExtensionTemplate> findOpcodes(
			final List<ValueType> types, final EncogProgramContext context,
			final boolean includeTerminal, final boolean includeFunction) {
		final List<ProgramExtensionTemplate> result = new ArrayList<ProgramExtensionTemplate>();

		for (final ProgramExtensionTemplate temp : this.opcodes) {
			for (final ValueType rtn : types) {
				// it is a possible return type, but given our variables, is it
				// possible
				if (temp.isPossibleReturnType(context, rtn)) {
					if (temp.getChildNodeCount() == 0 && includeTerminal) {
						result.add(temp);
					} else if (includeFunction) {
						result.add(temp);
					}
				}
			}
		}

		return result;
	}

	/**
	 * This method is used when parsing an expression. Consider x≥2. The parser
	 * first sees the &gt; symbol. But it must also consider the =. So we first
	 * look for a 2-char operator, in this case there is one.
	 * 
	 * @param ch1
	 *            The first character of the potential operator.
	 * @param ch2
	 *            The second character of the potential operator. Zero if none.
	 * @return The expression template for the operator found.
	 */
	public ProgramExtensionTemplate findOperator(final char ch1, final char ch2) {
		ProgramExtensionTemplate result = null;

		// if ch2==0 then we are only looking for a single char operator.
		// this is rare, but supported.
		if (ch2 == 0) {
			return findOperatorExact("" + ch1);
		}

		// first, see if we can match an operator with both ch1 and ch2
		result = findOperatorExact("" + ch1 + ch2);

		if (result == null) {
			// okay no 2-char operators matched, so see if we can find a single
			// char
			result = findOperatorExact("" + ch1);
		}

		// return the operator if we have one.
		return result;
	}

	/**
	 * Find an exact match on opcode.
	 * 
	 * @param str
	 *            The string to match.
	 * @return The opcode found.
	 */
	private ProgramExtensionTemplate findOperatorExact(final String str) {
		for (final ProgramExtensionTemplate opcode : this.opcodes) {
			// only consider non-unary operators
			if (opcode.getNodeType()==NodeType.OperatorLeft || opcode.getNodeType()==NodeType.OperatorRight) {
				if (opcode.getName().equals(str)) {
					return opcode;
				}
			}
		}
		return null;
	}

	/**
	 * Get the specified opcode.
	 * 
	 * @param theOpCode
	 *            The opcode index.
	 * @return The opcode.
	 */
	public ProgramExtensionTemplate getOpCode(final int theOpCode) {
		return this.opcodes.get(theOpCode);
	}

	/**
	 * @return The opcode list.
	 */
	public List<ProgramExtensionTemplate> getOpCodes() {
		return this.opcodes;
	}

	/**
	 * @return the templateMap
	 */
	public Map<String, ProgramExtensionTemplate> getTemplateMap() {
		return this.templateMap;
	}

	/**
	 * Determine if an opcode is in the function factory.
	 * 
	 * @param name
	 *            The name of the opcode.
	 * @param l
	 *            The argument count for the opcode.
	 * @return True if the opcode exists.
	 */
	public boolean isDefined(final String name, final int l) {
		final String key = EncogOpcodeRegistry.createKey(name, l);
		return this.templateMap.containsKey(key);
	}

	/**
	 * @return The number of opcodes.
	 */
	public int size() {
		return this.opcodes.size();
	}
}
