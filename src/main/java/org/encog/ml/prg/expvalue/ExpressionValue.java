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

import java.io.Serializable;

import org.encog.ml.ea.exception.EARuntimeError;
import org.encog.ml.prg.ExpressionError;

public class ExpressionValue implements Serializable {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	private final String stringValue;
	private final double floatValue;
	private final boolean boolValue;
	private final ValueType currentType;
	private final long intValue;
	private final int enumType;

	public ExpressionValue(ExpressionValue other) {
		switch (this.currentType = other.currentType) {
		case booleanType:
			this.boolValue = other.boolValue;
			this.floatValue = 0;
			this.stringValue = null;
			this.intValue = 0;
			this.enumType = -1;
			break;
		case floatingType:
			this.floatValue = other.floatValue;
			this.boolValue = false;
			this.stringValue = null;
			this.intValue = 0;
			this.enumType = -1;
			break;
		case intType:
			this.intValue = other.intValue;
			this.boolValue = false;
			this.floatValue = 0;
			this.stringValue = null;
			this.enumType = -1;
			break;
		case stringType:
			this.stringValue = other.stringValue;
			this.boolValue = false;
			this.floatValue = 0;
			this.intValue = 0;
			this.enumType = -1;
			break;
		case enumType:
			this.intValue = other.intValue;
			this.boolValue = false;
			this.floatValue = 0;
			this.stringValue = null;
			this.enumType = other.enumType;
			break;
		default:
			throw new ExpressionError("Unsupported type.");

		}
	}

	public ExpressionValue(String theValue) {
		this.stringValue = theValue;
		this.currentType = ValueType.stringType;
		this.boolValue = false;
		this.floatValue = 0;
		this.intValue = 0;
		this.enumType = -1;
	}

	public ExpressionValue(double theValue) {
		this.floatValue = theValue;
		this.currentType = ValueType.floatingType;
		this.boolValue = false;
		this.stringValue = null;
		this.intValue = 0;
		this.enumType = -1;
	}

	public ExpressionValue(boolean theValue) {
		this.boolValue = theValue;
		this.currentType = ValueType.booleanType;
		this.floatValue = 0;
		this.stringValue = null;
		this.intValue = 0;
		this.enumType = -1;
	}

	public ExpressionValue(long theValue) {
		this.intValue = theValue;
		this.currentType = ValueType.intType;
		this.boolValue = false;
		this.floatValue = 0;
		this.stringValue = null;
		this.enumType = -1;
	}

	public ExpressionValue(int enumType, long theValue) {
		this.intValue = theValue;
		this.currentType = ValueType.enumType;
		this.boolValue = false;
		this.floatValue = 0;
		this.stringValue = null;
		this.enumType = enumType;
	}

	public ExpressionValue(ValueType theType) {
		this.currentType = theType;
		this.intValue = 0;
		this.boolValue = false;
		this.floatValue = 0;
		this.stringValue = null;
		this.enumType = -1;
	}

	public ValueType getCurrentType() {
		return currentType;
	}

	public double toFloatValue() {
		switch (currentType) {
		case intType:
			return this.intValue;
		case floatingType:
			return this.floatValue;
		case booleanType:
			throw (new EARuntimeError(
					"Type Mismatch: can't convert float to boolean."));
		case stringType:
			try {
				return Double.parseDouble(this.stringValue);
			} catch (NumberFormatException ex) {
				throw (new EARuntimeError("Type Mismatch: can't convert "
						+ this.stringValue + " to floating point."));
			}
		case enumType:
			throw (new EARuntimeError(
					"Type Mismatch: can't convert enum to float."));
		default:
			throw (new EARuntimeError("Unknown type: " + this.currentType));
		}
	}

	public String toStringValue() {
		switch (currentType) {
		case intType:
			return "" + this.intValue;
		case floatingType:
			return "" + this.floatValue;
		case booleanType:
			return "" + this.boolValue;
		case stringType:
			return this.stringValue;
		case enumType:
			return "[" + this.enumType + ":" + this.intValue + "]";
		default:
			throw (new EARuntimeError("Unknown type: " + this.currentType));
		}
	}

	public boolean toBooleanValue() {
		switch (currentType) {
		case intType:
			throw (new EARuntimeError("Type Mismatch: can't convert " + this.intValue
					+ " to boolean."));
		case floatingType:
			throw (new EARuntimeError("Type Mismatch: can't convert " + this.floatValue
					+ " to boolean."));
		case booleanType:
			return this.boolValue;
		case stringType:
			throw (new EARuntimeError("Type Mismatch: can't convert " + this.stringValue
					+ " to boolean."));
		case enumType:
			throw (new EARuntimeError(
					"Type Mismatch: can't convert enum to boolean."));
		default:
			throw (new EARuntimeError("Unknown type: " + this.currentType));
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
		return this.currentType == ValueType.stringType;
	}

	public boolean isInt() {
		return this.currentType == ValueType.intType;
	}

	public long toIntValue() {
		switch (currentType) {
		case intType:
			return this.intValue;
		case floatingType:
			return (int)this.floatValue;
		case booleanType:
			throw (new EARuntimeError(
					"Type Mismatch: can't convert int to boolean."));
		case stringType:
			try {
				return Integer.parseInt(this.stringValue);
			} catch (NumberFormatException ex) {
				throw (new EARuntimeError("Type Mismatch: can't convert "
						+ this.stringValue + " to int."));
			}
		case enumType:
			return this.intValue;
		default:
			throw (new EARuntimeError("Unknown type: " + this.currentType));
		}
	}

	public boolean isFloat() {
		return this.currentType == ValueType.floatingType;
	}

	public boolean isEnum() {
		return this.currentType == ValueType.enumType;
	}

	/**
	 * @return the enumType
	 */
	public int getEnumType() {
		return enumType;
	}

	/**
	 * @return True, if the value is either int or float.
	 */
	public boolean isNumeric() {
		return isFloat() || isInt();
	}

	public boolean isBoolean() {
		return this.currentType == ValueType.booleanType;
	}

}
