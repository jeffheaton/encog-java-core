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
	private final boolean isEnum;
	private final int enumType;
	private final int enumValueCount;
	
	
	public VariableMapping(String theName, ValueType theVariableType, boolean theIsEnum,
			int theEnumType, int theEnumValueCount) {
		super();
		this.name = theName;
		this.variableType = theVariableType;
		this.isEnum = theIsEnum;
		this.enumType = theEnumType;
		this.enumValueCount = theEnumValueCount;
	}
	
	public VariableMapping(String theName, ValueType theVariableType) {
		this(theName, theVariableType, false, 0, 0);
	}
	
	
	/**
	 * @return the variableType
	 */
	public ValueType getVariableType() {
		return variableType;
	}
	/**
	 * @return the isEnum
	 */
	public boolean isEnum() {
		return isEnum;
	}
	/**
	 * @return the enumType
	 */
	public int getEnumType() {
		return enumType;
	}
	/**
	 * @return the enumValueCount
	 */
	public int getEnumValueCount() {
		return enumValueCount;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[VariableMapping: name=");
		result.append(this.name);
		result.append(",type=");
		result.append(this.variableType.toString());
		result.append(",enum=");
		result.append(this.isEnum);
		result.append(",enumType=");
		result.append(this.enumType);
		result.append(",enumCount=");
		result.append(this.enumValueCount);
		result.append("]");
		return result.toString();
	}

}
