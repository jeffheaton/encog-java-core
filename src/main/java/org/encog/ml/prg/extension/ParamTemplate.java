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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.encog.ml.ea.exception.EACompileError;
import org.encog.ml.prg.expvalue.ValueType;

/**
 * Provides a template for parameters to the opcodes. This defines the accepted
 * types and if type of a given parameter passes through to the return type.
 */
public class ParamTemplate implements Serializable {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Possible types for this parameter.
	 */
	private final Set<ValueType> possibleTypes = new HashSet<ValueType>();

	/**
	 * Is this a pass through argument. If so, then the return type of the
	 * parent opcode will depend on the actual type of this parameter.
	 */
	private boolean passThrough;

	/**
	 * Default constructor.
	 */
	public ParamTemplate() {
	}

	/**
	 * Add all known types.
	 */
	public void addAllTypes() {
		for (final ValueType t : ValueType.values()) {
			addType(t);
		}
	}

	/**
	 * Add the specified type.
	 * @param theType The type to add.
	 */
	public void addType(final String theType) {
		if (theType.equals("b")) {
			addType(ValueType.booleanType);
		} else if (theType.equals("e")) {
			addType(ValueType.enumType);
		} else if (theType.equals("f")) {
			addType(ValueType.floatingType);
		} else if (theType.equals("i")) {
			addType(ValueType.intType);
		} else if (theType.equals("s")) {
			addType(ValueType.stringType);
		} else if (theType.equals("*")) {
			addAllTypes();
		} else {
			throw new EACompileError("Unknown type: " + theType);
		}
	}

	/**
	 * Add a type using a type enum.
	 * @param theType The type to add.
	 */
	public void addType(final ValueType theType) {
		this.possibleTypes.add(theType);
	}

	/**
	 * Determine the possable argument types, given the parent types.
	 * @param parentTypes The parent types.
	 * @return The possable types.
	 */
	public List<ValueType> determineArgumentTypes(
			final List<ValueType> parentTypes) {
		if (isPassThrough()) {
			return parentTypes;
		}

		final List<ValueType> result = new ArrayList<ValueType>();
		result.addAll(getPossibleTypes());
		return result;
	}

	/**
	 * @return the possibleTypes
	 */
	public Set<ValueType> getPossibleTypes() {
		return this.possibleTypes;
	}

	/**
	 * @return the passThrough
	 */
	public boolean isPassThrough() {
		return this.passThrough;
	}

	/**
	 * @param passThrough
	 *            the passThrough to set
	 */
	public void setPassThrough(final boolean passThrough) {
		this.passThrough = passThrough;
	}

}
