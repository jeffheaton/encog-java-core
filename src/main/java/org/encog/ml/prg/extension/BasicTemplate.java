package org.encog.ml.prg.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.encog.ml.prg.ExpressionError;
import org.encog.ml.prg.ProgramNode;
import org.encog.util.SimpleParser;

public abstract class BasicTemplate implements ProgramExtensionTemplate {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	private final String name;
	private final boolean varValue;
	private final int dataSize;
	private final NodeType nodeType;
	private final int precedence;
	private final String signature;
	private final List<ParamTemplate> params = new ArrayList<ParamTemplate>();
	private final ParamTemplate returnValue;

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
			SimpleParser parser = new SimpleParser(theSignature);
			parser.eatWhiteSpace();
			this.name = parser.readToChars("(").trim();
			parser.advance();

			boolean done = false;
			while (!done) {
				if (parser.peek() == ')') {
					parser.advance();
					done = true;
				} else if (parser.peek() == '{') {
					this.params.add( readParam(parser) );
				} else {
					parser.advance();
				}
			}
			
			// get the return type
			parser.eatWhiteSpace();
			if( !parser.lookAhead(":") ) {
				throw new ExpressionError("Return type not specified.");
			}
			parser.advance();
			parser.eatWhiteSpace();
			this.returnValue = readParam(parser);
		}
	}

	public BasicTemplate(final String theSignature) {
		this(0, theSignature, NodeType.Function, false, 0);
	}

	private ParamTemplate readParam(SimpleParser parser) {
		ParamTemplate result = new ParamTemplate();

		if (!parser.lookAhead("{")) {
			throw new ExpressionError("Expected {");
		}
		parser.advance();

		boolean done = false;
		StringBuilder buffer = new StringBuilder();
		
		while (!done) {
			if (parser.peek() == '}') {
				done = true;
				parser.advance();
			} else if (parser.peek() == '{') {
				throw new ExpressionError("Unexpected {");
			} else if (parser.peek() == '{') {
				done = true;
				parser.advance();
			} else if( parser.peek() == ',' ) {
				result.addType(buffer.toString().trim().toLowerCase());
				parser.advance();
				buffer.setLength(0);
			}
			else {
				buffer.append(parser.readChar());
			}
		}
		
		String s = buffer.toString().trim();
		if( s.length()>0 ) {
			result.addType(s);
		}

		return result;
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
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isVariable() {
		return this.varValue;
	}

	@Override
	public int getDataSize() {
		return this.dataSize;
	}

	/**
	 * @return the nodeType
	 */
	@Override
	public NodeType getNodeType() {
		return nodeType;
	}

	@Override
	public int getPrecedence() {
		return this.precedence;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[BasicTemplate:");
		result.append(this.signature);
		result.append(",type=");
		result.append(this.nodeType.toString());
		result.append("argCount=");
		result.append(this.getChildNodeCount());
		result.append("]");
		return result.toString();
	}

	/**
	 * @return the params
	 */
	@Override
	public List<ParamTemplate> getParams() {
		return params;
	}

	/**
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}

	@Override
	public void randomize(final Random rnd, final ProgramNode actual,
			final double minValue, final double maxValue) {
	}

	/**
	 * @return the returnValue
	 */
	@Override
	public ParamTemplate getReturnValue() {
		return returnValue;
	}
	
	
}
