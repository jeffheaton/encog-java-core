/*
 * Encog Neural Network and Bot Library for Java v1.x
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008, Heaton Research Inc., and individual contributors.
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
package org.encog.bot.spider.workload;

import org.encog.bot.BotError;

/**
 * WorkloadException: This exception is thrown when the workload manager
 * encounters an error.
 */
public class WorkloadError extends BotError {

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct a WorkloadException with the specified message.
	 * 
	 * @param message
	 *            The exception message.
	 */
	public WorkloadError(final String message) {
		super(message);
	}

	/**
	 * Called to wrap another exception as a WorkloadError.
	 * 
	 * @param t
	 *            An exception to be wrapped.
	 */
	public WorkloadError(final Throwable t) {
		super(t);
	}
}
