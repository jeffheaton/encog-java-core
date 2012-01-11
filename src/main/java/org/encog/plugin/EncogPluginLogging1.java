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
package org.encog.plugin;


/**
 * A plugin that supports logging.  This is a version 1 plugin.
 * 
 */
public interface EncogPluginLogging1 extends EncogPluginBase {

	/**
	 * @return The current log level.
	 */
	int getLogLevel();

	/**
	 * Log a message at the specified level.
	 * @param level The level to log at.
	 * @param message The message to log.
	 */
	void log(int level, String message);

	/**
	 * Log a throwable at the specified level.
	 * @param level The level to log at.
	 * @param t The error to log.
	 */
	void log(int level, Throwable t);

}
