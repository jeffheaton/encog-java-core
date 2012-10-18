package org.encog.parse.expression;

import java.util.ArrayList;
import java.util.List;

import org.encog.parse.expression.expvalue.ExpressionValue;
import org.encog.parse.expression.extension.ExpressionExtension;
import org.encog.parse.expression.operators.ExpressionOperatorAdd;
import org.encog.parse.expression.operators.ExpressionOperatorAnd;
import org.encog.parse.expression.operators.ExpressionOperatorDiv;
import org.encog.parse.expression.operators.ExpressionOperatorEqualTo;
import org.encog.parse.expression.operators.ExpressionOperatorGreaterThan;
import org.encog.parse.expression.operators.ExpressionOperatorGreaterThanEqual;
import org.encog.parse.expression.operators.ExpressionOperatorLessThan;
import org.encog.parse.expression.operators.ExpressionOperatorLessThanEqual;
import org.encog.parse.expression.operators.ExpressionOperatorMul;
import org.encog.parse.expression.operators.ExpressionOperatorOr;
import org.encog.parse.expression.operators.ExpressionOperatorPow;
import org.encog.parse.expression.operators.ExpressionOperatorSub;
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
				target = new ExpressionOperatorSub(target, t);
			} else if (ch == '+') {
				ExpressionTreeElement t = expr1();
				target = new ExpressionOperatorAdd(target, t);
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

		if (!(nextchar > 0 && ("/*<>=&|".indexOf(nextchar) != -1)))
			return target;

		while (nextchar > 0 && ("/*<>=&|".indexOf(nextchar) != -1)) {
			switch (this.parser.readChar()) {
			case '*':
				return new ExpressionOperatorMul(target, expr1p5());

			case '/':
				return new ExpressionOperatorDiv(target, expr1p5());
			case '<':
				if( this.parser.peek()=='=' ) {					
					this.parser.advance();
					return new ExpressionOperatorLessThanEqual(target, expr1p5());
				} 
				return new ExpressionOperatorLessThan(target, expr1p5());
			case '>':
				if( this.parser.peek()=='=' ) {					
					this.parser.advance();
					return new ExpressionOperatorGreaterThanEqual(target, expr1p5());
				}
				return new ExpressionOperatorGreaterThan(target, expr1p5());
			case '=':
				return new ExpressionOperatorEqualTo(target, expr1p5());
			case '&':
				return new ExpressionOperatorAnd(target, expr1p5());
			case '|':
				return new ExpressionOperatorOr(target, expr1p5());
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
			throw new ExpressionError("Invalid function call: " + this.parser.getLine());
		}
		this.parser.advance();
		
		ExpressionTreeFunction fn = null;
		
		for(ExpressionExtension extension: this.holder.getExtensions()) {
			fn = extension.factorFunction(this.holder, name, args);
			if( fn!=null ) {
				break;
			}
		}
		
		if( fn!=null ) {
			return fn;
		} else {
			throw new ExpressionError("Undefined function: " + name);
		}
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
			
			if( varName.toString().equals("true")) {
				return new ExpressionTreeConst(new ExpressionValue(true));
			} else if( varName.toString().equals("false")) {
				return new ExpressionTreeConst(new ExpressionValue(false));
			}
			else if( this.parser.peek()!='(' ) {			
				return new ExpressionTreeVariable(this.holder,varName.toString());
			} else {
				return parseFunction(varName.toString());
			}
		} else if ((this.parser.peek() == '+') || (this.parser.peek() == '-')
				|| Character.isDigit(this.parser.peek())
				|| (this.parser.peek() == '.') )
			target = parseConstant();
		else if (this.parser.peek() == '(') {
			this.parenCount++;
			this.parser.advance();
			target = expr();
			if (this.parser.peek() == ')') {
				this.parenCount--;
				this.parser.advance();
			}
		} 
		else if( this.parser.peek() == '\"' ) {
			target = this.parseString();
		} else
			throw (new ExpressionError("Syntax error"));

		while (this.parser.peek() == '^') {
			this.parser.advance();
			return new ExpressionOperatorPow(target, expr1p5());
		}
		return target;
	}

	private ExpressionTreeConst parseString() {
		StringBuilder str = new StringBuilder();

		char ch;

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
		return new ExpressionTreeConst(new ExpressionValue(str.toString()));
	}

	private ExpressionTreeElement parseConstant() {
		double value, exponent;
		boolean neg = false;
		String str = "";
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

		while (Character.isDigit(this.parser.peek()))
			value = (10.0 * value) + (this.parser.readChar() - '0');

		// Optional fractional
		if (this.parser.peek() == '.') {
			isFloat = true;
			this.parser.advance();

			int i = 1;
			while (Character.isDigit(this.parser.peek())) {
				double f = (this.parser.readChar() - '0');
				f/=Math.pow(10.0, i);
				value+=f;
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
				isFloat = true;
				exponent = -exponent;
			}

			value = value * Math.pow(10, exponent);
		}

		if (neg)
			value = -value;
		
		if( isFloat )
			return new ExpressionTreeConst(new ExpressionValue((double)value));
		else
			return new ExpressionTreeConst(new ExpressionValue((int)value));
	}

	public ExpressionHolder getHolder() {
		return holder;
	}
	
	

}
