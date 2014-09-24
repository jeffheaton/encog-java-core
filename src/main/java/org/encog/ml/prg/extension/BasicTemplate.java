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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.encog.EncogError;
import org.encog.ml.ea.exception.EACompileError;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ValueType;
import org.encog.util.SimpleParser;

/**
 * A basic template.
 */
public abstract class BasicTemplate implements ProgramExtensionTemplate {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of this opcode.
	 */
	private final String name;

	/**
	 * True if this opcode has a variable value, other than variance of its
	 * child nodes.
	 */
	private final boolean varValue;

	/**
	 * The amount of data that is stored with this node.
	 */
	private final int dataSize;

	/**
	 * The node type.
	 */
	private final NodeType nodeType;

	/**
	 * The precedence.
	 */
	private final int precedence;

	/**
	 * The opcode signature.
	 */
	private final String signature;

	/**
	 * The parameters.
	 */
	private final List<ParamTemplate> params = new ArrayList<ParamTemplate>();

	/**
	 * The return value.
	 */
	private final ParamTemplate returnValue;

	/**
	 * Construct a basic template object.
	 * 
	 * @param thePrecedence
	 *            The precedence.
	 * @param theSignature
	 *            The opcode signature.
	 * @param theType
	 *            The opcode type.
	 * @param isVariable
	 *            True, if this opcode is a variable.
	 * @param theDataSize
	 *            The data size kept for this opcode.
	 */
	public BasicTemplate(final int thePrecedence, final String theSignature,
			final NodeType theType, final boolean isVariable,
			final int theDataSize) {
		this.precedence = thePrecedence;
		this.signature = theSignature;
		this.varValue = isVariable;
		this.dataSize = theDataSize;
		this.nodeType = theType;

		if (theSignature.trim().equals("(")) {
			// special case, we add a left-paren for the shunting yard alg.
			this.name = theSignature;
			this.returnValue = null;
		} else {
			// non-special case, find the name of the function/operator
			final SimpleParser parser = new SimpleParser(theSignature);
			boolean pass = false;

			parser.eatWhiteSpace();
			this.name = parser.readToChars("(").trim();
			parser.advance();

			boolean done = false;
			while (!done) {
				if (parser.peek() == ')') {
					parser.advance();
					done = true;
				} else if (parser.peek() == ':') {
					parser.advance();
					pass = true;
				} else if (parser.peek() == '{') {
					final ParamTemplate temp = readParam(parser);
					temp.setPassThrough(pass);
					pass = false;
					this.params.add(temp);
				} else {
					parser.advance();
					if( parser.eol() ) {
						throw new EncogError("Invalid opcode template.");
					}
				}
			}

			// get the return type
			parser.eatWhiteSpace();
			if (!parser.lookAhead(":")) {
				throw new EACompileError("Return type not specified.");
			}
			parser.advance();
			parser.eatWhiteSpace();
			this.returnValue = readParam(parser);
		}
	}

	/**
	 * Construct a function based on the provided signature.
	 * 
	 * @param theSignature
	 *            The signature.
	 */
	public BasicTemplate(final String theSignature) {
		this(0, theSignature, NodeType.Function, false, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getChildNodeCount() {
		return this.params.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getDataSize() {
		return this.dataSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeType getNodeType() {
		return this.nodeType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ParamTemplate> getParams() {
		return this.params;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPrecedence() {
		return this.precedence;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ParamTemplate getReturnValue() {
		return this.returnValue;
	}

	/**
	 * @return the signature
	 */
	public String getSignature() {
		return this.signature;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPossibleReturnType(final EncogProgramContext context,
			final ValueType rtn) {
		return this.returnValue.getPossibleTypes().contains(rtn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isVariable() {
		return this.varValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void randomize(final Random rnd, final List<ValueType> desiredTypes,
			final ProgramNode actual, final double minValue,
			final double maxValue) {
	}

	/**
	 * Read the specified parameter.
	 * 
	 * @param parser
	 *            The parser to use.
	 * @return The parsed parameter.
	 */
	private ParamTemplate readParam(final SimpleParser parser) {
		final ParamTemplate result = new ParamTemplate();

		if (!parser.lookAhead("{")) {
			throw new EACompileError("Expected {");
		}
		parser.advance();

		boolean done = false;
		final StringBuilder buffer = new StringBuilder();

		while (!done) {
			if (parser.peek() == '}') {
				done = true;
				parser.advance();
			} else if (parser.peek() == '{') {
				throw new EACompileError("Unexpected {");
			} else if (parser.peek() == '{') {
				done = true;
				parser.advance();
			} else if (parser.peek() == ',') {
				result.addType(buffer.toString().trim().toLowerCase());
				parser.advance();
				buffer.setLength(0);
			} else {
				buffer.append(parser.readChar());
			}
		}

		final String s = buffer.toString().trim();
		if (s.length() > 0) {
			result.addType(s);
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[BasicTemplate:");
		result.append(this.signature);
		result.append(",type=");
		result.append(this.nodeType.toString());
		result.append(",argCount=");
		result.append(getChildNodeCount());
		result.append("]");
		return result.toString();
	}

}
