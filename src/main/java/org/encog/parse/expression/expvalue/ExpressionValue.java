package org.encog.parse.expression.expvalue;

import org.encog.parse.expression.ExpressionError;

public class ExpressionValue {
	
	private String stringValue;
	private double floatValue;
	private boolean boolValue;
	private ValueType currentType;
	private int intValue;
	
	public ExpressionValue(String theValue) {
		setValue( theValue );
	}
	
	public ExpressionValue(double theValue) {
		setValue( theValue );
	}
	
	public ExpressionValue(boolean theValue) {
		setValue( theValue );
	}
	
	public ExpressionValue(int theValue) {
		setValue( theValue );
	}
	
	public ValueType getCurrentType() {
		return currentType;
	}
	public void setCurrentType(ValueType currentType) {
		this.currentType = currentType;
	}

	public void setValue(String stringValue) {
		this.stringValue = stringValue;
		this.currentType = ValueType.stringType;
	}
	
	public void setValue(double floatValue) {
		this.floatValue = floatValue;
		this.currentType = ValueType.floatingType;
	}
	
	public void setValue(int intValue) {
		this.intValue = intValue;
		this.currentType = ValueType.intType;
	}
	
	public void setValue(boolean boolValue) {
		this.boolValue = boolValue;
		this.currentType = ValueType.booleanType;
	}

	public double toFloatValue() {
		switch(currentType) {
			case intType:
				return this.intValue;
			case floatingType:
				return this.floatValue;
			case booleanType:
				throw(new ExpressionError("Type Mismatch: can't convert float to boolean."));
			case stringType:
				try {
					return Double.parseDouble(this.stringValue);
				} catch(NumberFormatException ex) {
					throw(new ExpressionError("Type Mismatch: can't convert "+this.stringValue+" to floating point."));
				}
			default:
				throw(new ExpressionError("Unknown type: " + this.currentType));
		}
	}
	
	public String toStringValue() {
		switch(currentType) {
			case intType:
				return ""+this.intValue;
			case floatingType:
				return ""+this.floatValue;
			case booleanType:
				return ""+this.boolValue;
			case stringType:
				return this.stringValue;
			default:
				throw(new ExpressionError("Unknown type: " + this.currentType));
		}
	}
	
	public String toBooleanValue() {
		switch(currentType) {
			case intType:
				throw(new ExpressionError("Type Mismatch: can't "+this.intValue+" to boolean."));
			case floatingType:
				throw(new ExpressionError("Type Mismatch: can't "+this.floatValue+" to boolean."));
			case booleanType:
				return ""+this.boolValue;
			case stringType:
				throw(new ExpressionError("Type Mismatch: can't "+this.stringValue+" to boolean."));
			default:
				throw(new ExpressionError("Unknown type: " + this.currentType));
		}
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[String: ");
		result.append("String Value: ");
		result.append(toStringValue());
		result.append("]");
		return result.toString();
	}

	public boolean isString() {
		return this.currentType==ValueType.stringType;
	}

	public boolean isInt() {
		return this.currentType==ValueType.intType;
	}

	public int toIntValue() {
		return(int)toFloatValue();
	}

}
