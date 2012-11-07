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

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ExpressionError;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.util.SimpleParser;

public class ParseCommonExpression {

	private final EncogProgram holder;
	private SimpleParser parser;
	private int parenCount;

	public ParseCommonExpression(final EncogProgram theHolder) {
		this.holder = theHolder;
	}

	private ProgramNode expr() {
		char sign;
		ProgramNode target;

		this.parser.eatWhiteSpace();

		if ((this.parser.peek() == '+') || (this.parser.peek() == '-')) {
			sign = this.parser.readChar();
		} else {
			sign = '+';
		}

		target = expr1();
		this.parser.eatWhiteSpace();

		if (sign == '-') {
			target = this.holder.getFunctions().factorFunction("-", new ProgramNode[] { target } );
		}

		while ((this.parser.peek() == '+') || (this.parser.peek() == '-')) {
			final char ch = this.parser.readChar();

			if (ch == '-') {
				final ProgramNode t = expr1();
				target = this.holder.getFunctions().factorFunction("-", new ProgramNode[] { target, t} );
			} else if (ch == '+') {
				final ProgramNode t = expr1();
				target = this.holder.getFunctions().factorFunction("+", new ProgramNode[] { target, t} );
			}
		}

		return target;
	}

	private ProgramNode expr1() {
		ProgramNode target;
		this.parser.eatWhiteSpace();

		target = expr1p5();
		this.parser.eatWhiteSpace();

		final char nextchar = this.parser.peek();

		if (!((nextchar > 0) && ("/*<>=&|".indexOf(nextchar) != -1))) {
			return target;
		}

		switch (this.parser.readChar()) {
		case '*':
			return this.holder.getFunctions().factorFunction("*",
					new ProgramNode[] { target, expr1() });
		case '/':
			return this.holder.getFunctions().factorFunction("/",
					new ProgramNode[] { target, expr1() });
		case '<':
			if (this.parser.peek() == '=') {
				this.parser.advance();
				return this.holder.getFunctions().factorFunction("<=",
						new ProgramNode[] { target, expr1() });
			}
			return this.holder.getFunctions().factorFunction("<",
					new ProgramNode[] { target, expr1() });
		case '>':
			if (this.parser.peek() == '=') {
				this.parser.advance();
				return this.holder.getFunctions().factorFunction(">=",
						new ProgramNode[] { target, expr1() });
			}
			return this.holder.getFunctions().factorFunction(">",
					new ProgramNode[] { target, expr1() });
		case '=':
			return this.holder.getFunctions().factorFunction("=",
					new ProgramNode[] { target, expr1() });
		case '&':
			return this.holder.getFunctions().factorFunction("&",
					new ProgramNode[] { target, expr1() });
		case '|':
			return this.holder.getFunctions().factorFunction("|",
					new ProgramNode[] { target, expr1() });
		}

		return target;

	}

	private ProgramNode expr1p5() {
		ProgramNode target = null;

		this.parser.eatWhiteSpace();

		if ((Character.toUpperCase(this.parser.peek()) >= 'A')
				&& (Character.toUpperCase(this.parser.peek()) <= 'Z')) {
			final StringBuilder varName = new StringBuilder();
			while ((Character.toUpperCase(this.parser.peek()) >= 'A')
					&& (Character.toUpperCase(this.parser.peek()) <= 'Z')) {
				varName.append(this.parser.readChar());
			}

			this.parser.eatWhiteSpace();

			if (varName.toString().equals("true")) {
				ProgramNode v = this.holder.getFunctions().factorFunction("#const", new ProgramNode[] {} );
				v.getExpressionData()[0] = new ExpressionValue(true);
				return v;
			} else if (varName.toString().equals("false")) {
				ProgramNode v = this.holder.getFunctions().factorFunction("#const", new ProgramNode[] {} );
				v.getExpressionData()[0] = new ExpressionValue(false);
				return v;
			} else if (this.parser.peek() != '(') {
				ProgramNode v;
				// either a variable or a const, see which
				if( this.holder.getFunctions().isDefined(varName.toString(),0) ) {
					v = this.holder.getFunctions().factorFunction(varName.toString(), new ProgramNode[] {} );
				} else {
					this.holder.setVariable(varName.toString(), null);
					v = this.holder.getFunctions().factorFunction("#var", new ProgramNode[] {} );
					v.getIntData()[0] = this.holder.getVariableIndex(varName.toString());
				}
				return v;
			} else {
				return parseFunction(varName.toString());
			}
		} else if ((this.parser.peek() == '+') || (this.parser.peek() == '-')
				|| Character.isDigit(this.parser.peek())
				|| (this.parser.peek() == '.')) {
			target = parseConstant();
		} else if (this.parser.peek() == '(') {
			this.parenCount++;
			this.parser.advance();
			target = expr();
			if (this.parser.peek() == ')') {
				this.parenCount--;
				this.parser.advance();
			}
		} else if (this.parser.peek() == '\"') {
			target = parseString();
		} else {
			throw (new ExpressionError("Syntax error"));
		}

		while (this.parser.peek() == '^') {
			this.parser.advance();
			return this.holder.getFunctions().factorFunction("^", new ProgramNode[] { target, expr1p5()} );
		}
		return target;
	}

	public EncogProgram getHolder() {
		return this.holder;
	}

	public ProgramNode parse(final String expression) {
		this.parenCount = 0;
		this.parser = new SimpleParser(expression);
		final ProgramNode result = expr();
		if (this.parenCount != 0) {
			throw new ExpressionError("Unbalanced parentheses");
		}
		return result;
	}

	private ProgramNode parseConstant() {
		double value, exponent;
		boolean neg = false;
		char sign = '+';
		boolean isFloat = false;

		switch (this.parser.peek()) {
		case '-':
			this.parser.advance();
			neg = true;
			break;

		case '+':
			this.parser.advance();
			break;
		}

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

		ProgramNode v = this.holder.getFunctions().factorFunction("#const", new ProgramNode[] {} );
		
		if (isFloat) {
			v.getExpressionData()[0] = new ExpressionValue(value);
		} else {
			v.getExpressionData()[0] = new ExpressionValue((int) value);
		}
		
		return v;
	}

	private ProgramNode parseFunction(final String name) {
		final ParseCommonExpression expParser = new ParseCommonExpression(this.holder);
		final StringBuilder currentExpression = new StringBuilder();
		final List<ProgramNode> args = new ArrayList<ProgramNode>();
		int pcnt = 0;

		this.parser.advance();
		this.parser.eatWhiteSpace();

		while (!this.parser.eol()
				&& !((pcnt == 0) && (this.parser.peek() == ')'))) {
			if (((this.parser.peek() == ',') || this.parser.isWhiteSpace())
					&& (pcnt == 0)) {
				args.add(expParser.parse(currentExpression.toString().trim()));
				currentExpression.setLength(0);
				this.parser.advance();
			} else {
				final char ch = this.parser.readChar();
				currentExpression.append(ch);
				if (ch == '(') {
					pcnt++;
				} else if (ch == ')') {
					pcnt--;
				}
			}
		}

		if (currentExpression.length() > 0) {
			args.add(expParser.parse(currentExpression.toString().trim()));
		}

		if (this.parser.peek() != ')') {
			throw new ExpressionError("Invalid function call: "
					+ this.parser.getLine());
		}
		this.parser.advance();
		return this.holder.getFunctions().factorFunction(name,toArgArray(args));
	}
	
	private ProgramNode[] toArgArray(List<ProgramNode> nodes) {
		ProgramNode[] result = new ProgramNode[nodes.size()];
		for(int i=0;i<nodes.size();i++) {
			result[i] = nodes.get(i);
		}
		return result;
	}

	private ProgramNode parseString() {
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
		
		ProgramNode v = this.holder.getFunctions().factorFunction("#const", new ProgramNode[] {} );
		v.getExpressionData()[0] = new ExpressionValue(str.toString());
		return v;
	}

}
