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
package org.encog.app.generate.program;

/**
 * A function argument for Encog created code.
 * 
 */
public class EncogProgramArg {

	/**
	 * The type of this argument.
	 */
	final EncogArgType type;

	/**
	 * The value of this argument.
	 */
	final Object value;

	/**
	 * Construct the argument. Default to float type.
	 * 
	 * @param value
	 *            The argument value.
	 */
	public EncogProgramArg(final double value) {
		this(EncogArgType.Float, "" + value);
	}

	/**
	 * Construct the argument.
	 * 
	 * @param type
	 *            The type of argument.
	 * @param value
	 *            The value of the argument.
	 */
	public EncogProgramArg(final EncogArgType type, final Object value) {
		super();
		this.type = type;
		this.value = value;
	}

	/**
	 * Construct a floating point arguement from an integer.
	 * 
	 * @param value
	 */
	public EncogProgramArg(final int value) {
		this(EncogArgType.Float, "" + value);
	}

	/**
	 * Construct using an object.
	 * 
	 * @param argValue
	 *            The argument value.
	 */
	public EncogProgramArg(final Object argValue) {
		this(EncogArgType.ObjectType, argValue);
	}

	/**
	 * Construct a string argument.
	 * 
	 * @param value
	 *            The string value.
	 */
	public EncogProgramArg(final String value) {
		this(EncogArgType.String, value);
	}

	/**
	 * @return The type of argument.
	 */
	public EncogArgType getType() {
		return this.type;
	}

	/**
	 * @return The value.
	 */
	public Object getValue() {
		return this.value;
	}
}
