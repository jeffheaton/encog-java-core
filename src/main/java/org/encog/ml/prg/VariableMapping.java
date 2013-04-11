package org.encog.ml.prg;

import java.io.Serializable;

import org.encog.ml.prg.expvalue.ValueType;

public class VariableMapping implements Serializable {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;
	private final String name;
	private final ValueType variableType;
	private final int enumType;
	private final int enumValueCount;

	public VariableMapping(final String theName, final ValueType theVariableType) {
		this(theName, theVariableType, 0, 0);
	}

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
