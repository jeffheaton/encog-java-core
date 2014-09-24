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
package org.encog.ml.prg;

import java.io.Serializable;

import org.encog.ml.prg.expvalue.ValueType;

/**
 * A variable mapping defines the type for each of the variables in an Encog
 * program.
 */
public class VariableMapping implements Serializable {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the variable.
	 */
	private final String name;

	/**
	 * The variable type.
	 */
	private final ValueType variableType;

	/**
	 * If this is an enum, what is the type.
	 */
	private final int enumType;

	/**
	 * The count for this given enum. If this is not an enum, then value is not
	 * used.
	 */
	private final int enumValueCount;

	/**
	 * Construct a variable mapping for a non-enum type.
	 * 
	 * @param theName
	 *            The variable name.
	 * @param theVariableType
	 *            The variable type.
	 */
	public VariableMapping(final String theName, final ValueType theVariableType) {
		this(theName, theVariableType, 0, 0);
	}

	/**
	 * Construct a variable mapping.
	 * 
	 * @param theName
	 *            The name of the variable.
	 * @param theVariableType
	 *            The type of the variable.
	 * @param theEnumType
	 *            The enum type.
	 * @param theEnumValueCount
	 *            The number of values for an enum.
	 */
	public VariableMapping(final String theName,
			final ValueType theVariableType, final int theEnumType,
			final int theEnumValueCount) {
		super();
		this.name = theName;
		this.variableType = theVariableType;
		this.enumType = theEnumType;
		this.enumValueCount = theEnumValueCount;
	}

	/**
	 * @return the enumType
	 */
	public int getEnumType() {
		return this.enumType;
	}

	/**
	 * @return the enumValueCount
	 */
	public int getEnumValueCount() {
		return this.enumValueCount;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the variableType
	 */
	public ValueType getVariableType() {
		return this.variableType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[VariableMapping: name=");
		result.append(this.name);
		result.append(",type=");
		result.append(this.variableType.toString());
		result.append(",enumType=");
		result.append(this.enumType);
		result.append(",enumCount=");
		result.append(this.enumValueCount);
		result.append("]");
		return result.toString();
	}

}
