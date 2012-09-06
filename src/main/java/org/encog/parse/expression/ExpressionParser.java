package org.encog.parse.expression;

import java.util.ArrayList;
import java.util.List;

import org.encog.util.SimpleParser;

public class ExpressionParser {
	
	private final ExpressionHolder holder;
	private SimpleParser parser;
	private int parenCount;
	
	public ExpressionParser(ExpressionHolder theHolder) {
		this.holder = theHolder;
	}

	public ExpressionTreeElement parse(String expression) {
		this.parenCount = 0;
		this.parser = new SimpleParser(expression);
		ExpressionTreeElement result = expr();
		if( parenCount!=0 ) {
			throw new ExpressionError("Unbalanced parentheses");
		}
		return result;
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
	
	private ExpressionTreeFunction parseFunction(String name) {
		ExpressionParser expParser = new ExpressionParser(this.holder);
		StringBuilder currentExpression = new StringBuilder();
		List<ExpressionTreeElement> args = new ArrayList<ExpressionTreeElement>();
		int pcnt = 0;
		
		this.parser.advance();
		this.parser.eatWhiteSpace();
		
		while( !parser.eol() && !(pcnt==0 && this.parser.peek()==')') ) {
			if( (parser.peek()==',' || parser.isWhiteSpace()) && (pcnt==0) ) {
				args.add(expParser.parse(currentExpression.toString().trim()));
				currentExpression.setLength(0);
				this.parser.advance();
			} else {
				char ch = parser.readChar();
				currentExpression.append(ch);
				if( ch=='(' ) {
					pcnt++;
				} else if( ch==')' ) {
					pcnt--;
				}
			}
		}
		
		if( currentExpression.length()>0 ) {
			args.add(expParser.parse(currentExpression.toString().trim()));
		}
		
		if( this.parser.peek()!=')' ) {
			throw new ExpressionError("Invalid function call");
		}
		this.parser.advance();
		
		return new ExpressionTreeFunction(this.holder,name,args);
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
				return new ExpressionTreeVariable(this.holder,varName.toString());
			} else {
				return parseFunction(varName.toString());
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

	public ExpressionHolder getHolder() {
		return holder;
	}
	
	

}
