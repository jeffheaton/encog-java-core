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
package org.encog.parse.expression.common;

import org.encog.ml.ea.exception.EACompileError;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.BasicTemplate;
import org.encog.ml.prg.extension.NodeType;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.util.SimpleParser;
import org.encog.util.datastruct.StackObject;

/**
 * This class is used to process a common format equation (in-fix) into the tree
 * format that Encog uses. To do this I make use of the shunting yard algorithm.
 * 
 * One important note on definitions. I consider an operator to be simply a
 * special type of function. Really, an operator is just a shorthand for writing
 * certain types of functions. Therefore I do not distinguish between functions
 * and operators in this implementation.
 * 
 * References:
 * 
 * http://en.wikipedia.org/wiki/Shunting-yard_algorithm
 **/
public class ParseCommonExpression {

	private final EncogProgram holder;
	private SimpleParser parser;
	private ProgramNode rootNode;
	private boolean unary;
	private ProgramExtensionTemplate LEFT_PAREN = new BasicTemplate(
			ProgramExtensionTemplate.NO_PREC, "(", NodeType.None, false, 0) {
		@Override
		public ExpressionValue evaluate(ProgramNode actual) {
			return null;
		}
	};

	private StackObject<ProgramExtensionTemplate> functionStack = new StackObject<ProgramExtensionTemplate>(
			100);
	private StackObject<ProgramNode> outputStack = new StackObject<ProgramNode>(
			100);

	public ParseCommonExpression(final EncogProgram theHolder) {
		this.holder = theHolder;
	}

	/**
	 * Push a leaf onto the output stack.
	 * 
	 * @param leaf
	 *            The leaf to push onto the output stack.
	 **/
	private void outputQueue(ProgramNode leaf) {
		outputStack.push(leaf);
	}

	private void outputQueue(ProgramExtensionTemplate opp) {
		if (opp == this.LEFT_PAREN) {
			throw new EACompileError("Unmatched parentheses");
		}

		ProgramNode[] args = new ProgramNode[opp.getChildNodeCount()];

		for (int i = args.length - 1; i >= 0; i--) {
			if (this.outputStack.isEmpty()) {
				throw new EACompileError("Not enough arguments");
			}
			args[i] = this.outputStack.pop();
		}

		this.rootNode = this.holder.getFunctions().factorProgramNode(opp,
				this.holder, args);
		outputStack.push(rootNode);
	}

	private void functionQueue(ProgramExtensionTemplate f) {

		// while there is an operator token, o2, at the top of the stack, and
		// either o1 is left-associative and o1 has precedence less than or
		// equal to that of o2,
		// or o1 has precedence less than that of o2,

		while (!this.functionStack.isEmpty()
				&& this.functionStack.peek().getNodeType() != NodeType.None
				&& ((f.getNodeType() == NodeType.OperatorLeft && f
						.getPrecedence() >= this.functionStack.peek()
						.getPrecedence()) || f.getPrecedence() > this.functionStack
						.peek().getPrecedence())) {
			outputQueue(this.functionStack.pop());
		}

		functionStack.push(f);
	}

	private void handleNumeric() {
		double value, exponent;
		char sign = '+';
		boolean isFloat = false;
		boolean neg = false;

		value = 0.0;
		exponent = 0;

		// should we just make this negative, due to an unary minus?
		if (!this.functionStack.isEmpty()
				&& this.functionStack.peek() == StandardExtensions.EXTENSION_NEG) {
			this.functionStack.pop();
			neg = true;
		}

		// whole number part

		while (Character.isDigit(this.parser.peek())) {
			value = (10.0 * value) + (this.parser.readChar() - '0');
		}

		// Optional fractional
		if (this.parser.peek() == '.') {
			isFloat = true;
			this.parser.advance();

			int i = 1;
			while (Character.isDigit(this.parser.peek())) {
				double f = (this.parser.readChar() - '0');
				f /= Math.pow(10.0, i);
				value += f;
				i++;
			}
		}

		// Optional exponent

		if (Character.toUpperCase(this.parser.peek()) == 'E') {
			this.parser.advance();

			if ((this.parser.peek() == '+') || (this.parser.peek() == '-')) {
				sign = this.parser.readChar();
			}

			while (Character.isDigit(this.parser.peek())) {
				exponent = (int) (10.0 * exponent)
						+ (this.parser.readChar() - '0');
			}

			if (sign == '-') {
				isFloat = true;
				exponent = -exponent;
			}

			value = value * Math.pow(10, exponent);
		}

		if (neg) {
			value = -value;
		}

		ProgramNode v = this.holder.getFunctions().factorProgramNode("#const",
				holder, new ProgramNode[] {});

		if (isFloat) {
			v.getData()[0] = new ExpressionValue(value);
		} else {
			v.getData()[0] = new ExpressionValue((int) value);
		}

		outputQueue(v);
	}

	private void handleAlpha(boolean neg) {
		final StringBuilder varName = new StringBuilder();
		while (Character.isLetterOrDigit(this.parser.peek())) {
			varName.append(this.parser.readChar());
		}

		this.parser.eatWhiteSpace();

		if (varName.toString().equals("true")) {
			if (neg) {
				throw new EACompileError("Invalid negative sign.");
			}
			ProgramNode v = this.holder.getFunctions().factorProgramNode("#const",
					holder, new ProgramNode[] {});
			v.getData()[0] = new ExpressionValue(true);
			outputQueue(v);
		} else if (varName.toString().equals("false")) {
			if (neg) {
				throw new EACompileError("Invalid negative sign.");
			}
			ProgramNode v = this.holder.getFunctions().factorProgramNode("#const",
					holder, new ProgramNode[] {});
			v.getData()[0] = new ExpressionValue(false);
			outputQueue(v);
		} else if (this.parser.peek() != '(') {
			ProgramNode v;
			// either a variable or a const, see which
			if (this.holder.getFunctions().isDefined(varName.toString(), 0)) {
				v = this.holder.getFunctions().factorProgramNode(
						varName.toString(), holder, new ProgramNode[] {});
			} else {
				this.holder.getVariables().setVariable(varName.toString(),
						new ExpressionValue(0));
				v = this.holder.getFunctions().factorProgramNode("#var", holder,
						new ProgramNode[] {});
				v.getData()[0] = new ExpressionValue((int) this.holder.getVariables()
						.getVariableIndex(varName.toString()));
			}

			if (neg) {
				v = this.holder.getFunctions().factorProgramNode("-", holder,
						new ProgramNode[] { v });
			}
			outputQueue(v);
		} else {
			ProgramExtensionTemplate temp = this.holder.getFunctions()
					.findFunction(varName.toString());
			if (temp == null) {
				throw new EACompileError("Undefined function: "
						+ varName.toString());
			}
			functionQueue(temp);
		}
	}

	private void handleSymbol() {
		char ch1 = this.parser.readChar();

		// handle unary
		if (this.unary) {
			if (ch1 == '+') {
				return;
			} else if (ch1 == '-') {
				this.functionStack.push(StandardExtensions.EXTENSION_NEG);
				return;
			}
		}

		// handle regular operator
		char ch2 = 0;
		if (!this.parser.eol()) {
			ch2 = this.parser.peek();
		}

		ProgramExtensionTemplate temp = this.holder.getFunctions()
				.findOperator(ch1, ch2);

		// did we find anything?
		if (temp != null) {
			// if we found a 2-char operator, then advance beyond the 2nd
			// char
			if (temp.getName().length() > 1) {
				this.parser.advance();
			}
			functionQueue(temp);
		} else {
			throw new EACompileError("Unknown symbol: " + ch1);
		}

	}

	private void handleString() {
		final StringBuilder str = new StringBuilder();

		char ch;

		if (this.parser.peek() == '\"') {
			this.parser.advance();
		}
		do {
			ch = this.parser.readChar();
			if (ch == 34) {
				// handle double quote
				if (this.parser.peek() == 34) {
					this.parser.advance();
					str.append(ch);
					ch = this.parser.readChar();
				}
			} else {
				str.append(ch);
			}
		} while ((ch != 34) && (ch > 0));

		if (ch != 34) {
			throw (new EACompileError("Unterminated string"));
		}

		ProgramNode v = this.holder.getFunctions().factorProgramNode("#const",
				holder, new ProgramNode[] {});
		v.getData()[0] = new ExpressionValue(str.toString());
		outputQueue(v);
	}

	private void handleRightParen() {
		// move past the paren
		this.parser.advance();

		// Until the token at the top of the stack is a left parenthesis, pop
		// operators off the stack onto the output queue.

		while (this.functionStack.peek() != this.LEFT_PAREN) {
			outputQueue(this.functionStack.pop());
		}

		// Pop the left parenthesis from the stack, but not onto the output
		// queue.
		this.functionStack.pop();

		// If the token at the top of the stack is a function token, pop it onto
		// the output queue.
		if (!this.functionStack.isEmpty()
				&& this.functionStack.peek().getNodeType() == NodeType.Function) {
			outputQueue(this.functionStack.pop());
		}

		// If the stack runs out without finding a left parenthesis, then there
		// are mismatched parentheses.
	}

	private void handleFunctionSeparator() {
		// advance past
		this.parser.advance();

		// Until the token at the top of the stack is a left parenthesis,
		// pop operators off the stack onto the output queue.
		while (this.functionStack.peek() != this.LEFT_PAREN) {
			outputQueue(this.functionStack.pop());
		}
		
		// If no left parentheses are encountered, either the separator was
		// misplaced or parentheses were mismatched.

	}

	public ProgramNode parse(final String expression) {
		this.parser = new SimpleParser(expression);
		this.unary = true;

		while (!parser.eol()) {
			parser.eatWhiteSpace();
			char ch = parser.peek();
			if (ch == '.' || Character.isDigit(ch)) {
				handleNumeric();
				this.unary = false;
			} else if (Character.isLetter(ch)) {
				handleAlpha(false);
				this.unary = false;
			} else if (ch == '(') {
				this.parser.advance();
				this.functionStack.push(LEFT_PAREN);
				this.unary = true;
			} else if (ch == ')') {
				handleRightParen();
				this.unary = false;
			} else if ("<>^*/+-=&|".indexOf(ch) != -1) {
				handleSymbol();
				this.unary = true;
			} else if (ch == '\"') {
				handleString();
				this.unary = false;
			} else if (ch == ',') {
				handleFunctionSeparator();
				this.unary = true;
			} else {
				throw new EACompileError("Unparsable character: " + ch);
			}
		}

		// pop off any functions still on the stack
		while (!this.functionStack.isEmpty()) {
			ProgramExtensionTemplate f = this.functionStack.pop();
			outputQueue(f);
		}

		// were there no operators?
		if (this.rootNode == null) {
			this.rootNode = this.outputStack.pop();
		}

		return this.rootNode;
	}
}
