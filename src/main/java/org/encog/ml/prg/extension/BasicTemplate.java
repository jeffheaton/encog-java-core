package org.encog.ml.prg.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ExpressionError;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ValueType;
import org.encog.util.SimpleParser;

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
	 * True if this opcode has a variable value, other than variance of its child nodes.
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
	 * @param thePrecedence The precedence.
	 * @param theSignature The opcode signature.
	 * @param theType The opcode type.
	 * @param isVariable True, if this opcode is a variable.
	 * @param theDataSize The data size kept for this opcode.
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
			SimpleParser parser = new SimpleParser(theSignature);
			boolean pass = false;
			
			parser.eatWhiteSpace();
			this.name = parser.readToChars("(").trim();
			parser.advance();

			boolean done = false;
			while (!done) {
				if (parser.peek() == ')') {
					parser.advance();
					done = true;
				} else if( parser.peek()==':') {
					parser.advance();
					pass = true;
				} else if (parser.peek() == '{') {
					ParamTemplate temp = readParam(parser);
					temp.setPassThrough(pass);
					pass = false;
					this.params.add( temp );
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

	/**
	 * Construct a function based on the provided signature.
	 * @param theSignature The signature.
	 */
	public BasicTemplate(final String theSignature) {
		this(0, theSignature, NodeType.Function, false, 0);
	}

	/**
	 * Read the specified parameter.
	 * @param parser The parser to use.
	 * @return The parsed parameter.
	 */
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
	public int getDataSize() {
		return this.dataSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeType getNodeType() {
		return nodeType;
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
	 * {@inheritDoc}
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void randomize(final Random rnd, final List<ValueType> desiredTypes, final ProgramNode actual,
			final double minValue, final double maxValue) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ParamTemplate getReturnValue() {
		return returnValue;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPossibleReturnType(EncogProgramContext context, ValueType rtn) {
		return this.returnValue.getPossibleTypes().contains(rtn);
	}
	
}
