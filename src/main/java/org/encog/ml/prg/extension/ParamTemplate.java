package org.encog.ml.prg.extension;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.encog.ml.prg.ExpressionError;
import org.encog.ml.prg.expvalue.ValueType;

public class ParamTemplate implements Serializable {
	private final Set<ValueType> possibleTypes = new HashSet<ValueType>(); 
	private boolean passThrough;
	
	public ParamTemplate() {
	}
	
	public void addType(String theType) {
		if( theType.equals("b") ) {
			addType(ValueType.booleanType);
		} else if( theType.equals("e") ) {
			addType(ValueType.enumType);
		} else if( theType.equals("f") ) {
			addType(ValueType.floatingType);
		} else if( theType.equals("i") ) {
			addType(ValueType.intType);
		} else if( theType.equals("s") ) {
			addType(ValueType.stringType);
		} else if( theType.equals("*") ) {
			addAllTypes();
		} else {
			throw new ExpressionError("Unknown type: " + theType);
		}
	}
	
	public void addType(ValueType theType) {
		this.possibleTypes.add(theType);
	}
	
	public void addAllTypes() {
		for( ValueType t : ValueType.values()) {
			addType(t);
		}
	}

	/**
	 * @return the possibleTypes
	 */
	public Set<ValueType> getPossibleTypes() {
		return possibleTypes;
	}

	/**
	 * @return the passThrough
	 */
	public boolean isPassThrough() {
		return passThrough;
	}

	/**
	 * @param passThrough the passThrough to set
	 */
	public void setPassThrough(boolean passThrough) {
		this.passThrough = passThrough;
	}
	
	
	
}
