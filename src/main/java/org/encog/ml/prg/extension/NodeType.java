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
