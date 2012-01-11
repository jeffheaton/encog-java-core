/*
 * Encog(tm) Core v3.1 - Java Version
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
package org.encog.mathutil.matrices;

import org.encog.EncogError;

/**
 * Used by the matrix classes to indicate an error.
 */
public class MatrixError extends EncogError {

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = -8961386981267748942L;

	/**
	 * Construct this exception with a message.
	 * 
	 * @param message
	 *            The message for this exception.
	 */
	public MatrixError(final String message) {		
		super(message);
	}

	/**
	 * Construct this exception with another exception.
	 * 
	 * @param t
	 *            The other exception.
	 */
	public MatrixError(final Throwable t) {
		super(t);
	}

}
