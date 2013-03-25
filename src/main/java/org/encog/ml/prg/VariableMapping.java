package org.encog.ml.prg;

import java.io.Serializable;

import org.encog.ml.prg.expvalue.ValueType;

public class VariableMapping implements Serializable {
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

}
