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

import org.encog.ml.ea.exception.EARuntimeError;

/**
 * A division by zero.
 */
public class DivisionByZeroError extends EARuntimeError {
	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 1L;

	public DivisionByZeroError() {
		this("Division by zero");
	}

	/**
	 * Just a message.
	 * 
	 * @param msg
	 *            The message.
	 */
	public DivisionByZeroError(final String msg) {
		super(msg);
	}

	/**
	 * Message with a throwable.
	 * 
	 * @param msg
	 *            The message.
	 * @param t
	 *            The throwable.
	 */
	public DivisionByZeroError(final String msg, final Throwable t) {
		super(msg, t);
	}

	/**
	 * Just a throwable.
	 * 
	 * @param t
	 *            The throwable.
	 */
	public DivisionByZeroError(final Throwable t) {
		super(t);
	}
}
