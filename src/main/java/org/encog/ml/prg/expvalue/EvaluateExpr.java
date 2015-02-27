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

import org.encog.Encog;

/**
 * Simple utility class that performs some basic operations on ExpressionValue
 * objects.
 */
public final class EvaluateExpr {

	/**
	 * Perform an add on two expression values. a+b
	 * 
	 * @param a
	 *            The first argument.
	 * @param b
	 *            The second argument.
	 * @return The result of adding two numbers. Concat for strings. If one is a
	 *         string, the other is converted to string. If no string, then if
	 *         one is float, both are converted to int.
	 */
	public static ExpressionValue add(final ExpressionValue a,
			final ExpressionValue b) {
		if (a.isString() || b.isString()) {
			return new ExpressionValue(a.toStringValue() + b.toStringValue());
		}
		if (a.isInt() && b.isInt()) {
			return new ExpressionValue(a.toIntValue() + b.toIntValue());
		} else {
			return new ExpressionValue(a.toFloatValue() + b.toFloatValue());
		}
	}

	/**
	 * Perform a division on two expression values. a/b An Encog division by
	 * zero exception can occur. If one param is a float, the other is converted
	 * to a float.
	 * 
	 * @param a
	 *            The first argument, must be numeric.
	 * @param b
	 *            The second argument, must be numeric.
	 * @return The result of the operation.
	 */
	public static ExpressionValue div(final ExpressionValue a,
			final ExpressionValue b) {
		if (a.isInt() && b.isInt()) {
			final long i = b.toIntValue();
			if (i == 0) {
				throw new DivisionByZeroError();
			}
			return new ExpressionValue(a.toIntValue() / i);
		}

		final double denom = b.toFloatValue();

		if (Math.abs(denom) < Encog.DEFAULT_DOUBLE_EQUAL) {
			throw new DivisionByZeroError();
		}

		return new ExpressionValue(a.toFloatValue() / denom);
	}

	/**
	 * Perform an equal on two expressions. Booleans, ints and strings must
	 * exactly equal. Floating point must be equal within the default Encog
	 * tolerance.
	 * 
	 * @param a
	 *            The first parameter to check.
	 * @param b
	 *            The second parameter to check.
	 * @return True/false.
	 */
	public static ExpressionValue equ(final ExpressionValue a,
			final ExpressionValue b) {

		if (a.getExpressionType() == ValueType.booleanType) {
			return new ExpressionValue(a.toBooleanValue() == b.toBooleanValue());
		} else if (a.getExpressionType() == ValueType.enumType) {
			return new ExpressionValue(a.toIntValue() == b.toIntValue()
					&& a.getEnumType() == b.getEnumType());
		} else if (a.getExpressionType() == ValueType.stringType) {
			return new ExpressionValue(a.toStringValue().equals(
					b.toStringValue()));
		} else {
			final double diff = Math.abs(a.toFloatValue() - b.toFloatValue());
			return new ExpressionValue(diff < Encog.DEFAULT_DOUBLE_EQUAL);
		}
	}

	/**
	 * Perform a multiply on two expression values. a*b If one param is a float,
	 * the other is converted to a float.
	 * 
	 * @param a
	 *            The first argument, must be numeric.
	 * @param b
	 *            The second argument, must be numeric.
	 * @return The result of the operation.
	 */
	public static ExpressionValue mul(final ExpressionValue a,
			final ExpressionValue b) {
		if (a.isInt() && b.isInt()) {
			return new ExpressionValue(a.toIntValue() * b.toIntValue());
		}
		return new ExpressionValue(a.toFloatValue() * b.toFloatValue());
	}

	/**
	 * Perform a non-equal on two expressions. Booleans, ints and strings must
	 * exactly non-equal. Floating point must be non-equal within the default
	 * Encog tolerance.
	 * 
	 * @param a
	 *            The first parameter to check.
	 * @param b
	 *            The second parameter to check.
	 * @return True/false.
	 */
	public static ExpressionValue notequ(final ExpressionValue a,
			final ExpressionValue b) {
		if (a.getExpressionType() == ValueType.booleanType) {
			return new ExpressionValue(a.toBooleanValue() != b.toBooleanValue());
		} else if (a.getExpressionType() == ValueType.enumType) {
			return new ExpressionValue(a.toIntValue() != b.toIntValue()
					&& a.getEnumType() == b.getEnumType());
		} else if (a.getExpressionType() == ValueType.stringType) {
			return new ExpressionValue(!a.toStringValue().equals(
					b.toStringValue()));
		} else {
			final double diff = Math.abs(a.toFloatValue() - b.toFloatValue());
			return new ExpressionValue(diff > Encog.DEFAULT_DOUBLE_EQUAL);
		}
	}

	/**
	 * Perform a protected div on two expression values. a/b If one param is a
	 * float, the other is converted to a float.
	 * 
	 * @param a
	 *            The first argument, must be numeric.
	 * @param b
	 *            The second argument, must be numeric.
	 * @return The result of the operation.
	 */
	public static ExpressionValue pow(final ExpressionValue a,
			final ExpressionValue b) {
		if (a.isInt() && b.isInt()) {
			return new ExpressionValue(Math.pow(a.toIntValue(), b.toIntValue()));
		}
		return new ExpressionValue(Math.pow(a.toFloatValue(), b.toFloatValue()));
	}

	/**
	 * Perform a protected div on two expression values. a/b Division by zero
	 * results in 1.
	 * 
	 * @param a
	 *            The first argument, must be numeric.
	 * @param b
	 *            The second argument, must be numeric.
	 * @return The result of the operation.
	 */
	public static ExpressionValue protectedDiv(final ExpressionValue a,
			final ExpressionValue b) {
		if (a.isInt() && b.isInt()) {
			final long i = b.toIntValue();
			if (i == 0) {
				return new ExpressionValue(1);
			}
			return new ExpressionValue(a.toIntValue() / i);
		}

		final double denom = b.toFloatValue();

		if (Math.abs(denom) < Encog.DEFAULT_DOUBLE_EQUAL) {
			return new ExpressionValue(1);
		}

		return new ExpressionValue(a.toFloatValue() / denom);
	}

	/**
	 * Perform a subtract on two expression values. a-b If one param is a float,
	 * the other is converted to a float.
	 * 
	 * @param a
	 *            The first argument, must be numeric.
	 * @param b
	 *            The second argument, must be numeric.
	 * @return The result of the operation.
	 */
	public static ExpressionValue sub(final ExpressionValue a,
			final ExpressionValue b) {
		if (a.isInt() && b.isInt()) {
			return new ExpressionValue(a.toIntValue() - b.toIntValue());
		} else {
			return new ExpressionValue(a.toFloatValue() - b.toFloatValue());
		}
	}

	/**
	 * Private constructor.
	 */
	private EvaluateExpr() {

	}
}
