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
package org.encog.parse.expression.common;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ExpressionError;
import org.encog.ml.prg.exception.EncogProgramCompileError;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.util.SimpleParser;

public class ParseCommonExpression {

	private final EncogProgram program;
	private SimpleParser parser;
	private int parenCount;

	public ParseCommonExpression(final EncogProgram theProgram) {
		this.program = theProgram;
	}

	private void expr() {
		boolean neg = false;
		this.parser.eatWhiteSpace();

		if ((this.parser.peek() == '+') || (this.parser.peek() == '-')) {
			neg = this.parser.readChar()=='-';
		}
		
		expr1();
		this.parser.eatWhiteSpace();

		if (neg ) {
			this.program.writeNode(StandardExtensions.OPCODE_NEG);
		}

		while ((this.parser.peek() == '+') || (this.parser.peek() == '-')) {
			final char ch = this.parser.readChar();

			if (ch == '-') {
				expr1();
				this.program.writeNode(StandardExtensions.OPCODE_SUB );
			} else if (ch == '+') {
				expr1();
				this.program.writeNode(StandardExtensions.OPCODE_ADD );
			}
		}
	}

	private void expr1() {
		this.parser.eatWhiteSpace();

		expr1p5();
		this.parser.eatWhiteSpace();

		char nextchar = this.parser.peek();

		while ((nextchar > 0) && ("/*<>=&|".indexOf(nextchar) != -1)) {
			
			switch (this.parser.readChar()) {
			case '*':
				expr1p5();
				this.program.writeNode(StandardExtensions.OPCODE_MUL);
				break;
			case '/':
				expr1p5();
				this.program.writeNode(StandardExtensions.OPCODE_DIV);
				break;
			case '<':
				if (this.parser.peek() == '=') {
					this.parser.advance();
					expr1p5();
					this.program.writeNode(StandardExtensions.OPCODE_LTE);
				} else {
					expr1p5();
					this.program.writeNode(StandardExtensions.OPCODE_LT);
				}
				break;
			case '>':
				if (this.parser.peek() == '=') {
					this.parser.advance();
					expr1p5();
					this.program.writeNode(StandardExtensions.OPCODE_GTE);
				} else {
					expr1p5();
					this.program.writeNode(StandardExtensions.OPCODE_GT);
				}
				break;
			case '=':
				expr1p5();
				this.program.writeNode(StandardExtensions.OPCODE_EQUAL);
				break;
			case '&':
				expr1p5();
				this.program.writeNode(StandardExtensions.OPCODE_AND);
				break;
			case '|':
				expr1p5();
				this.program.writeNode(StandardExtensions.OPCODE_OR);
				break;
			}
			
			nextchar = this.parser.peek();
		}
	}

	private void expr1p5() {
		boolean neg = false;

		this.parser.eatWhiteSpace();

		if ((this.parser.peek() == '+') || (this.parser.peek() == '-')) {
			neg = this.parser.readChar()=='-';
		}
		
		if ((Character.toUpperCase(this.parser.peek()) >= 'A')
				&& (Character.toUpperCase(this.parser.peek()) <= 'Z')) {
			final StringBuilder buffer = new StringBuilder();
			while (Character.isLetterOrDigit(this.parser.peek()) ) {
				buffer.append(this.parser.readChar());
			}

			this.parser.eatWhiteSpace();
			String name = buffer.toString();

			if (name.equals("true")) {
				if( neg ) {
					throw new EncogProgramCompileError("Invalid negative sign.");
				}
				this.program.writeConstNode(true);
				//return;
			} else if (name.equals("false")) {
				if( neg ) {
					throw new EncogProgramCompileError("Invalid negative sign.");
				}
				this.program.writeConstNode(false);
				//return;
			} else if (this.parser.peek() != '(') {
				// either a variable or a const, see which
				
				if( this.program.getFunctions().getKnownConsts().containsKey(name)) {
					// known const
					this.program.writeNode(this.program.getFunctions().getKnownConsts().get(name));
				} else {
					// var
					this.program.getContext().defineVariable(name);
					this.program.getVariables().defineVariable(name);
					this.program.writeNodeVar(name);
				} 
				
				if (neg ) {
					this.program.writeNode(StandardExtensions.OPCODE_NEG);
				}
				
				//return;
			} else {
				parseFunction(name);
			}
		} else if ((this.parser.peek() == '+') || (this.parser.peek() == '-')
				|| Character.isDigit(this.parser.peek())
				|| (this.parser.peek() == '.')) {
			parseConstant(neg);
		} else if (this.parser.peek() == '(') {
			this.parenCount++;
			this.parser.advance();
			expr();
			this.parser.eatWhiteSpace();
			if (this.parser.peek() == ')') {
				this.parenCount--;
				this.parser.advance();
				this.parser.eatWhiteSpace();
			}
		} else if (this.parser.peek() == '\"') {
			parseString();
		} else {
			throw (new ExpressionError("Syntax error: " + this.parser.peek() ));
		}

		while (this.parser.peek() == '^') {
			this.parser.advance();
			expr1p5();
			this.program.writeNode(StandardExtensions.OPCODE_POW );
			return;
		}
	}

	public EncogProgram getHolder() {
		return this.program;
	}

	public void parse(final String expression) {
		this.program.setProgramCounter(0);
		this.parenCount = 0;
		this.parser = new SimpleParser(expression);
		expr();
		if (this.parenCount != 0) {
			throw new ExpressionError("Unbalanced parentheses");
		}
		
	}

	private void parseConstant(boolean neg) {
		double value, exponent;
		char sign = '+';
		boolean isFloat = false;

		value = 0.0;
		exponent = 0;

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

		if (isFloat) {
			this.program.writeConstNode(value);
		} else {
			this.program.writeConstNode((int)value);
		}
	}

	private void parseFunction(final String name) {
		int acnt = 0;

		this.parser.advance();
		boolean first = true;

		while (!this.parser.eol()
				&& (this.parser.peek() != ')')) {
			this.parser.eatWhiteSpace();
			if( !first ) {
				if( this.parser.peek()!=',' ) {
					throw new EncogProgramCompileError("Expected , in function call.");
				}
				this.parser.advance();
			}
			first = false;
			expr();
			
			acnt++;
		}

		if (this.parser.peek() != ')') {
			throw new ExpressionError("Invalid function call: "
					+ this.parser.getLine());
		}
		this.parser.advance();
		this.program.writeNode(this.program.getFunctions().getOpCode(name,acnt));
	}

	private void parseString() {
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
			throw (new ExpressionError("Unterminated string"));
		}
		
		this.program.writeNodeString(str.toString());
	}

}
