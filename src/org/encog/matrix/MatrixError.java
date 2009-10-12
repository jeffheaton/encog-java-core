/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.matrix;

import org.encog.EncogError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used by the matrix classes to indicate an error.
 */
public class MatrixError extends EncogError {

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = -8961386981267748942L;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
