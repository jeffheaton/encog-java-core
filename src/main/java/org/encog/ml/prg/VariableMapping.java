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
