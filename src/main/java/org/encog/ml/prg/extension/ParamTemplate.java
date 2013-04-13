package org.encog.ml.prg.extension;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.encog.ml.prg.ExpressionError;
import org.encog.ml.prg.expvalue.ValueType;

public class ParamTemplate implements Serializable {
	private final Set<ValueType> possibleTypes = new HashSet<ValueType>();
	private boolean passThrough;

	public ParamTemplate() {
	}

	public void addAllTypes() {
		for (final ValueType t : ValueType.values()) {
			addType(t);
		}
	}

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
			throw new ExpressionError("Unknown type: " + theType);
		}
	}

	public void addType(final ValueType theType) {
		this.possibleTypes.add(theType);
	}

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
