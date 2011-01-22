/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Provides simple logging access to the JDK logging system. Encog uses slf4j to
 * abstract which log it uses. However, by default Encog uses JDK logging. This
 * class can be used to manage JDK logging.
 * 
 * @author jheaton
 * 
 */
public final class Logging {

	/**
	 * Log everything to the console.
	 */
	public static void allConsoleLogging() {
		Logging.setConsoleLevel(Level.FINEST);
		Logging.getRootLogger().setLevel(Level.FINEST);

	}

	/**
	 * @return The handler for the console logger.
	 */
	public static Handler getConsoleHandler() {
		final Handler[] handlers = Logging.getRootLogger().getHandlers();
		for (final Handler element : handlers) {
			if (element instanceof ConsoleHandler) {
				return element;
			}
		}
		return null;
	}

	/**
	 * @return The root logger.
	 */
	public static Logger getRootLogger() {
		final Logger logger = LogManager.getLogManager().getLogger("");
		return logger;
	}

	/**
	 * Set the logging level for console.
	 * 
	 * @param level
	 *            The logging level.
	 */
	public static void setConsoleLevel(final Level level) {
		Logging.getConsoleHandler().setLevel(level);
	}

	/**
	 * Stop logging to the console.
	 */
	public static void stopConsoleLogging() {
		Logging.setConsoleLevel(Level.OFF);
	}

	/**
	 * Private constructor.
	 */
	private Logging() {
	}
}
