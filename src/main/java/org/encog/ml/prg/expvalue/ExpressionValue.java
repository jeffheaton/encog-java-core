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

import org.encog.ml.prg.ExpressionError;

public class ExpressionValue implements Serializable {

	private String stringValue;
	private double floatValue;
	private boolean boolValue;
	private ValueType currentType;
	private long intValue;

	public ExpressionValue(final boolean theValue) {
		setValue(theValue);
	}

	public ExpressionValue(final double theValue) {
		setValue(theValue);
	}

	public ExpressionValue(final ExpressionValue other) {
		setValue(other);
	}

	public ExpressionValue(final long theValue) {
		setValue(theValue);
	}

	public ExpressionValue(final String theValue) {
		setValue(theValue);
	}

	public ValueType getCurrentType() {
		return this.currentType;
	}

	public boolean isFloat() {
		return this.currentType == ValueType.floatingType;
	}

	public boolean isInt() {
		return this.currentType == ValueType.intType;
	}

	public boolean isString() {
		return this.currentType == ValueType.stringType;
	}

	public void setCurrentType(final ValueType currentType) {
		this.currentType = currentType;
	}

	public void setValue(final boolean boolValue) {
		this.boolValue = boolValue;
		this.currentType = ValueType.booleanType;
	}

	public void setValue(final double floatValue) {
		this.floatValue = floatValue;
		this.currentType = ValueType.floatingType;
	}

	public void setValue(final ExpressionValue expValue) {
		switch (this.currentType = expValue.currentType) {
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

	public void setValue(final long intValue) {
		this.intValue = intValue;
		this.currentType = ValueType.intType;
	}

	public void setValue(final String stringValue) {
		this.stringValue = stringValue;
		this.currentType = ValueType.stringType;
	}

	public boolean toBooleanValue() {
		switch (this.currentType) {
		case intType:
			throw new ExpressionError("Type Mismatch: can't " + this.intValue
					+ " to boolean.");
		case floatingType:
			throw new ExpressionError("Type Mismatch: can't " + this.floatValue
					+ " to boolean.");
		case booleanType:
			return this.boolValue;
		case stringType:
			throw new ExpressionError("Type Mismatch: can't "
					+ this.stringValue + " to boolean.");
		default:
			throw new ExpressionError("Unknown type: " + this.currentType);
		}
	}

	public double toFloatValue() {
		switch (this.currentType) {
		case intType:
			return this.intValue;
		case floatingType:
			return this.floatValue;
		case booleanType:
			throw new ExpressionError(
					"Type Mismatch: can't convert float to boolean.");
		case stringType:
			try {
				return Double.parseDouble(this.stringValue);
			} catch (final NumberFormatException ex) {
				throw new ExpressionError("Type Mismatch: can't convert "
						+ this.stringValue + " to floating point.");
			}
		default:
			throw new ExpressionError("Unknown type: " + this.currentType);
		}
	}

	public long toIntValue() {
		return (long) toFloatValue();
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[ExpressionValue: ");
		result.append("type: ");
		result.append(getCurrentType().toString());
		result.append(", String Value: ");
		result.append(toStringValue());
		result.append("]");
		return result.toString();
	}

	public String toStringValue() {
		switch (this.currentType) {
		case intType:
			return "" + this.intValue;
		case floatingType:
			return "" + this.floatValue;
		case booleanType:
			return "" + this.boolValue;
		case stringType:
			return this.stringValue;
		default:
			throw new ExpressionError("Unknown type: " + this.currentType);
		}
	}

}
