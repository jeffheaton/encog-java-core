package org.encog.ml.prg.extension;

/**
 * The node type. This mostly determines how opcodes are parsed and rendered.
 * Node types do not generally affect execution.
 */
public enum NodeType {
	/**
	 * A left associated operator. Operators are actually functions, however,
	 * setting to this type does affect how the opcode is rendered.
	 * http://en.wikipedia.org/wiki/Operator_associativity
	 */
	OperatorLeft,
	/**
	 * A right associated operator. Operators are actually functions, however,
	 * setting to this type does affect how the opcode is rendered.
	 * http://en.wikipedia.org/wiki/Operator_associativity
	 */
	OperatorRight,
	/**
	 * A leaf, or terminal node. No children.
	 */
	Leaf,
	/**
	 * A function.
	 */
	Function,
	/**
	 * An unary operator.
	 */
	Unary,
	/**
	 * Unknown.
	 */
	None;

	/**
	 * @return True, if this is an operator.
	 */
	public boolean isOperator() {
		return this == OperatorLeft || this == OperatorRight || this == Unary;
	}
}
