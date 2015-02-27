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
package org.encog.ml.prg.expvalue;

import java.io.Serializable;

import org.encog.ml.ea.exception.EARuntimeError;

/**
 * An EncogProgram expression value. These is how Encog stores variables and
 * calculates values.
 */
public class ExpressionValue implements Serializable {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * If the value is a string, this contains the value.
	 */
	private final String stringValue;

	/**
	 * If the value is a float, this contains the value.
	 */
	private final double floatValue;

	/**
	 * If the value is a boolean, this contains the value.
	 */
	private final boolean boolValue;

	/**
	 * The type of this expression.
	 */
	private final ValueType expressionType;

	/**
	 * If the value is an int, this contains the value.
	 */
	private final long intValue;

	/**
	 * If the value is an enum, this contains the value.
	 */
	private final int enumType;

	/**
	 * Construct a boolean expression.
	 * 
	 * @param theValue
	 *            The value to construct.
	 */
	public ExpressionValue(final boolean theValue) {
		this.boolValue = theValue;
		this.expressionType = ValueType.booleanType;
		this.floatValue = 0;
		this.stringValue = null;
		this.intValue = 0;
		this.enumType = -1;
	}

	/**
	 * Construct a boolean expression.
	 * 
	 * @param theValue
	 *            The value to construct.
	 */
	public ExpressionValue(final double theValue) {
		this.floatValue = theValue;
		this.expressionType = ValueType.floatingType;
		this.boolValue = false;
		this.stringValue = null;
		this.intValue = 0;
		this.enumType = -1;
	}

	/**
	 * Construct a expression based on an expression.
	 * 
	 * @param other
	 *            The value to construct.
	 */
	public ExpressionValue(final ExpressionValue other) {
		switch (this.expressionType = other.expressionType) {
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
			throw new EARuntimeError("Unsupported type.");

		}
	}

	/**
	 * Construct an enum expression.
	 * 
	 * @param theValue
	 *            The value to construct.
	 */
	public ExpressionValue(final int enumType, final long theValue) {
		this.intValue = theValue;
		this.expressionType = ValueType.enumType;
		this.boolValue = false;
		this.floatValue = 0;
		this.stringValue = null;
		this.enumType = enumType;
	}

	/**
	 * Construct an integer expression.
	 * 
	 * @param theValue
	 *            The value to construct.
	 */
	public ExpressionValue(final long theValue) {
		this.intValue = theValue;
		this.expressionType = ValueType.intType;
		this.boolValue = false;
		this.floatValue = 0;
		this.stringValue = null;
		this.enumType = -1;
	}

	/**
	 * Construct a string expression.
	 * 
	 * @param theValue
	 *            The value to construct.
	 */
	public ExpressionValue(final String theValue) {
		this.stringValue = theValue;
		this.expressionType = ValueType.stringType;
		this.boolValue = false;
		this.floatValue = 0;
		this.intValue = 0;
		this.enumType = -1;
	}

	/**
	 * Construct a value of the specified type.
	 * 
	 * @param theType
	 *            The value to construct.
	 */
	public ExpressionValue(final ValueType theType) {
		this.expressionType = theType;
		this.intValue = 0;
		this.boolValue = false;
		this.floatValue = 0;
		this.stringValue = null;
		this.enumType = -1;
	}

	/**
	 * @return the enumType
	 */
	public int getEnumType() {
		return this.enumType;
	}

	/**
	 * @return The expression type.
	 */
	public ValueType getExpressionType() {
		return this.expressionType;
	}

	/**
	 * @return True, if this is a boolean.
	 */
	public boolean isBoolean() {
		return this.expressionType == ValueType.booleanType;
	}

	/**
	 * @return True, if this is an enum.
	 */
	public boolean isEnum() {
		return this.expressionType == ValueType.enumType;
	}

	/**
	 * @return True, if this is a float.
	 */
	public boolean isFloat() {
		return this.expressionType == ValueType.floatingType;
	}

	/**
	 * @return True, if this is an int.
	 */
	public boolean isInt() {
		return this.expressionType == ValueType.intType;
	}

	/**
	 * @return True, if the value is either int or float.
	 */
	public boolean isNumeric() {
		return isFloat() || isInt();
	}

	/**
	 * @return True, if this is a string.
	 */
	public boolean isString() {
		return this.expressionType == ValueType.stringType;
	}

	/**
	 * @return The value as a boolean, or type mismatch if conversion is not
	 *         possible.
	 */
	public boolean toBooleanValue() {
		switch (this.expressionType) {
		case intType:
			throw new EARuntimeError("Type Mismatch: can't convert "
					+ this.intValue + " to boolean.");
		case floatingType:
			throw new EARuntimeError("Type Mismatch: can't convert "
					+ this.floatValue + " to boolean.");
		case booleanType:
			return this.boolValue;
		case stringType:
			throw new EARuntimeError("Type Mismatch: can't convert "
					+ this.stringValue + " to boolean.");
		case enumType:
			throw new EARuntimeError(
					"Type Mismatch: can't convert enum to boolean.");
		default:
			throw new EARuntimeError("Unknown type: " + this.expressionType);
		}
	}

	/**
	 * @return The value as a float, or type mismatch if conversion is not
	 *         possible.
	 */
	public double toFloatValue() {
		switch (this.expressionType) {
		case intType:
			return this.intValue;
		case floatingType:
			return this.floatValue;
		case booleanType:
			throw new EARuntimeError(
					"Type Mismatch: can't convert float to boolean.");
		case stringType:
			try {
				return Double.parseDouble(this.stringValue);
			} catch (final NumberFormatException ex) {
				throw new EARuntimeError("Type Mismatch: can't convert "
						+ this.stringValue + " to floating point.");
			}
		case enumType:
			throw new EARuntimeError(
					"Type Mismatch: can't convert enum to float.");
		default:
			throw new EARuntimeError("Unknown type: " + this.expressionType);
		}
	}

	/**
	 * @return The value as a int, or type mismatch if conversion is not
	 *         possible.
	 */
	public long toIntValue() {
		switch (this.expressionType) {
		case intType:
			return this.intValue;
		case floatingType:
			return (int) this.floatValue;
		case booleanType:
			throw new EARuntimeError(
					"Type Mismatch: can't convert int to boolean.");
		case stringType:
			try {
				return Long.parseLong(this.stringValue);
			} catch (final NumberFormatException ex) {
				throw new EARuntimeError("Type Mismatch: can't convert "
						+ this.stringValue + " to int.");
			}
		case enumType:
			return this.intValue;
		default:
			throw new EARuntimeError("Unknown type: " + this.expressionType);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[ExpressionValue: ");
		result.append("type: ");
		result.append(getExpressionType().toString());
		result.append(", String Value: ");
		result.append(toStringValue());
		result.append("]");
		return result.toString();
	}

	/**
	 * @return The value as a string, or type mismatch if conversion is not
	 *         possible.
	 */
	public String toStringValue() {
		switch (this.expressionType) {
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
			throw new EARuntimeError("Unknown type: " + this.expressionType);
		}
	}

}
