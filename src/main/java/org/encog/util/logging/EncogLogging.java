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
package org.encog.util.logging;

import org.encog.Encog;

/**
 * This class provides logging for Encog. Programs using Encog can make use of
 * it as well. All logging is passed on to the current logging plugin. By
 * default the SystemLoggingPlugin is used.
 * 
 */
public class EncogLogging {

	/**
	 * The lowest level log type. Debug logging provides low-level Encog
	 * diagnostics that may slow performance, but allow you to peer into the
	 * inner workings.
	 */
	public static final int LEVEL_DEBUG = 0;

	/**
	 * Info logging tells you when major processes start and stop.
	 */
	public static final int LEVEL_INFO = 1;

	/**
	 * Error level tells you about errors, less important to critical.
	 */
	public static final int LEVEL_ERROR = 2;

	/**
	 * Critical logging logs errors that cannot be recovered from.
	 */
	public static final int LEVEL_CRITICAL = 3;
	
	/**
	 * Logging is disabled at this level.
	 */
	public static final int LEVEL_DISABLE = 4;

	/**
	 * Log the message.
	 * 
	 * @param level
	 *            The level to log at.
	 * @param message
	 *            The message to log.
	 */
	public static final void log(final int level, final String message) {
		Encog.getInstance().getLoggingPlugin().log(level, message);
	}

	/**
	 * Log the error.
	 * 
	 * @param level
	 *            The level to log at.
	 * @param t
	 *            The exception to log.
	 */
	public static final void log(final int level, final Throwable t) {
		Encog.getInstance().getLoggingPlugin().log(level, t);
	}

	/**
	 * Log the error at ERROR level.
	 * 
	 * @param t
	 *            The exception to log.
	 */
	public static final void log(final Throwable t) {
		EncogLogging.log(EncogLogging.LEVEL_ERROR, t);
	}

	/**
	 * @return The current logging level.
	 */
	public final int getCurrentLevel() {
		return Encog.getInstance().getLoggingPlugin().getLogLevel();
	}

}
