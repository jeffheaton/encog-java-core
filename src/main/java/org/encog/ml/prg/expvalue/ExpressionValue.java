/*
 * Encog(tm) Core v3.2 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.ml.prg.expvalue;

import org.encog.ml.prg.ExpressionError;

public class ExpressionValue {
	
	private String stringValue;
	private double floatValue;
	private boolean boolValue;
	private ValueType currentType;
	private long intValue;
	
	public ExpressionValue(ExpressionValue other) {
		setValue(other);
	}
	
	public ExpressionValue(String theValue) {
		setValue( theValue );
	}
	
	public ExpressionValue(double theValue) {
		setValue( theValue );
	}
	
	public ExpressionValue(boolean theValue) {
		setValue( theValue );
	}
	
	public ExpressionValue(long theValue) {
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
	
	public void setValue(ExpressionValue expValue) {
		switch(this.currentType = expValue.currentType) {
		case booleanType:
			this.boolValue = expValue.boolValue;
			break;
		case floatingType:
			this.floatValue = expValue.floatValue;
			break;
		case intType:
			this.intValue = expValue.intValue;
			break;
		case stringType:
			this.stringValue = expValue.stringValue;
			break;
		default:
			throw new ExpressionError("Unsupported type.");
		
		}
	}
	
	public void setValue(double floatValue) {
		this.floatValue = floatValue;
		this.currentType = ValueType.floatingType;
	}
	
	public void setValue(long intValue) {
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
	
	public boolean toBooleanValue() {
		switch(currentType) {
			case intType:
				throw(new ExpressionError("Type Mismatch: can't "+this.intValue+" to boolean."));
			case floatingType:
				throw(new ExpressionError("Type Mismatch: can't "+this.floatValue+" to boolean."));
			case booleanType:
				return this.boolValue;
			case stringType:
				throw(new ExpressionError("Type Mismatch: can't "+this.stringValue+" to boolean."));
			default:
				throw(new ExpressionError("Unknown type: " + this.currentType));
		}
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[ExpressionValue: ");
		result.append("type: ");
		result.append(this.getCurrentType().toString());
		result.append(", String Value: ");
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

	public long toIntValue() {
		return(long)toFloatValue();
	}

	public boolean isFloat() {
		return this.currentType==ValueType.floatingType;
	}

}
