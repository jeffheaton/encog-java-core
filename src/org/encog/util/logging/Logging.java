/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
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
