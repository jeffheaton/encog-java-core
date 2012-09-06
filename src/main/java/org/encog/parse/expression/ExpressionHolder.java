package org.encog.parse.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.util.SimpleParser;

public class ExpressionHolder {

	private final SimpleParser parser;
	private final ExpressionTreeElement root;
	private int parenCount;
	private final Map<String,Double> memory = new HashMap<String,Double>();

	public ExpressionHolder(String expression) {
		this.parser = new SimpleParser(expression);
		this.root = expr();
		if( parenCount!=0 ) {
			throw new ExpressionError("Unbalanced parentheses");
		}

	}

	private ExpressionTreeElement expr() {
		char sign;
		ExpressionTreeElement target;

		parser.eatWhiteSpace();

		if ((parser.peek() == '+') || (parser.peek() == '-'))
			sign = parser.readChar();
		else
			sign = '+';

		target = expr1();
		parser.eatWhiteSpace();

		if (sign == '-') {
			target = new ExpressionTreeUnaryOperator("-", target);
		}

		while ((parser.peek() == '+') || (parser.peek() == '-')) {
			char ch = parser.readChar();
			
			if (ch=='-') {
				ExpressionTreeElement t = expr1();
				target = new ExpressionTreeOperator("-", target, t);
			} else if (ch == '+') {
				ExpressionTreeElement t = expr1();
				target = new ExpressionTreeOperator("+", target, t);
			}
		}
		
		return target;
	}

	private ExpressionTreeElement expr1() {
		ExpressionTreeElement target;
		ExpressionTreeElement t;
		ExpressionTreeElement v;

		this.parser.eatWhiteSpace();

		target = expr1p5();
		this.parser.eatWhiteSpace();

		char nextchar = this.parser.peek();

		if (!(nextchar > 0 && ("/*<>=".indexOf(nextchar) != -1)))
			return target;

		while (nextchar > 0 && ("/*<>=".indexOf(nextchar) != -1)) {
			switch (this.parser.readChar()) {
			case '*':
				return new ExpressionTreeOperator("*", target, expr1p5());

			case '/':
				return new ExpressionTreeOperator("/", target, expr1p5());
			}
		}
		return target;

	}
	
	private List<Double> parseFunction() {
		List<Double> result = new ArrayList<Double>();
		
		return result;
	}

	private ExpressionTreeElement expr1p5() {
		ExpressionTreeElement target = null;		

		this.parser.eatWhiteSpace();

		if (( Character.toUpperCase(this.parser.peek()) >= 'A') && (Character.toUpperCase(this.parser.peek()) <= 'Z')) {
			StringBuilder varName = new StringBuilder();
			while(( Character.toUpperCase(this.parser.peek()) >= 'A') && (Character.toUpperCase(this.parser.peek()) <= 'Z')) {
				varName.append(this.parser.readChar());
			}
			
			this.parser.eatWhiteSpace();
			
			if( this.parser.peek()!='(' ) {			
				return new ExpressionTreeVariable(this,varName.toString());
			} else {
				
			}
		} else if ((this.parser.peek() == '+') || (this.parser.peek() == '-')
				|| Character.isDigit(this.parser.peek())
				|| (this.parser.peek() == '.') || (this.parser.peek() == '\"'))
			target = parseConstant();
		else if (this.parser.peek() == '(') {
			this.parenCount++;
			this.parser.advance();
			target = expr();
			if (this.parser.peek() == ')') {
				this.parenCount--;
				this.parser.advance();
			}
		} else
			throw (new ExpressionError("Syntax error"));

		while (this.parser.peek() == '^') {
			this.parser.advance();
			return new ExpressionTreeOperator("^", target, expr1p5());
		}
		return target;
	}

	private String parseString() {
		StringBuilder str = new StringBuilder();

		char ch;
		int i = 0;

		if (this.parser.peek() == '\"')
			this.parser.advance();
		do {
			ch = this.parser.readChar();
			if (ch == 34) {
				// handle double quote
				if (this.parser.peek() == 34) {
					this.parser.advance();
					str.append(ch);
					ch = this.parser.readChar();
				}
			} else
				str.append(ch);
		} while ((ch != 34) && ch > 0);

		if (ch != 34)
			throw (new ExpressionError("Unterminated string"));
		return str.toString();
	}

	private ExpressionTreeElement parseConstant() {
		double value, exponent;
		boolean neg = false;
		String str = "";
		char sign = '+';

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

		while (Character.isDigit(this.parser.peek()))
			value = (10.0 * value) + (this.parser.readChar() - '0');

		// Optional fractional
		if (this.parser.peek() == '.') {
			this.parser.advance();

			int i = 1;
			while (Character.isDigit(this.parser.peek())) {
				value += (this.parser.readChar() - '0')/(10.0*i);
				i++;
			}
		}

		// Optional exponent

		if (Character.toUpperCase(this.parser.peek()) == 'E') {
			this.parser.advance();

			if ((this.parser.peek() == '+') || (this.parser.peek() == '-')) {				
				sign = this.parser.readChar();
			}

			while (Character.isDigit(parser.peek())) {
				exponent = (int) (10.0 * exponent) + (parser.readChar() - '0');
			}
			
			if (sign == '-') {
				exponent = -exponent;
			}

			value = value * Math.pow(10, exponent);
		}

		if (neg)
			value = -value;
		return new ExpressionTreeConst(value);
	}

	public static double parse(String str) {
		ExpressionHolder holder = new ExpressionHolder(str);
		return holder.evaluate();
	}

	public double evaluate() {
		return root.evaluate();
	}

	public void set(String name, double value) {
		this.memory.put(name, value);		
	}

	public boolean variableExists(String name) {
		return this.memory.containsKey(name);
	}

	public double get(String name) {
		return this.memory.get(name);
	}
}
