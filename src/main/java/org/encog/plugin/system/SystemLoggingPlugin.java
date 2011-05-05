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
package org.encog.plugin.system;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.plugin.EncogPluginType1;
import org.encog.util.logging.EncogLogging;

/**
 * This is the built-in logging plugin for Encog. This plugin provides simple
 * file and console logging.
 * 
 */
public class SystemLoggingPlugin implements EncogPluginType1 {

	/**
	 * Create a stack trace.
	 * 
	 * @param aThrowable
	 *            The throwable to create the trace for.
	 * @return The stack trace as a string.
	 */
	public static String getStackTrace(final Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

	/**
	 * The current level.
	 */
	private int currentLevel = EncogLogging.LEVEL_DISABLE;

	/**
	 * True if we are logging to the console.
	 */
	private boolean logConsole = false;

	/**
	 * Not used for this type of plugin.
	 * 
	 * @param gradients
	 *            Not used.
	 * @param layerOutput
	 *            Not used.
	 * @param weights
	 *            Not used.
	 * @param layerDelta
	 *            Not used.
	 * @param af
	 *            Not used.
	 * @param index
	 *            Not used.
	 * @param fromLayerIndex
	 *            Not used.
	 * @param fromLayerSize
	 *            Not used.
	 * @param toLayerIndex
	 *            Not used.
	 * @param toLayerSize
	 *            Not used.
	 */
	@Override
	public final void calculateGradient(final double[] gradients,
			final double[] layerOutput, final double[] weights,
			final double[] layerDelta, final ActivationFunction af,
			final int index, final int fromLayerIndex, final int fromLayerSize,
			final int toLayerIndex, final int toLayerSize) {

	}

	/**
	 * Not used for this type of plugin.
	 * 
	 * @param weights
	 *            Not used.
	 * @param layerOutput
	 *            Not used.
	 * @param startIndex
	 *            Not used.
	 * @param outputIndex
	 *            Not used.
	 * @param outputSize
	 *            Not used.
	 * @param inputIndex
	 *            Not used.
	 * @param inputSize
	 *            Not used.
	 * @return Not used.
	 */
	@Override
	public final int calculateLayer(final double[] weights,
			final double[] layerOutput, final int startIndex,
			final int outputIndex, final int outputSize, final int inputIndex,
			final int inputSize) {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getLogLevel() {
		return this.currentLevel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPluginDescription() {
		return "This is the built in logging for Encog, it logs "
				+ "to either a file or System.out";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPluginName() {
		return "HRI-System-Logging";
	}

	/**
	 * @return Returns the service type for this plugin. This plugin provides
	 *         the system calculation for layers and gradients. Therefore, this
	 *         plugin returns SERVICE_TYPE_CALCULATION.
	 */
	@Override
	public final int getPluginServiceType() {
		return EncogPluginType1.SERVICE_TYPE_LOGGING;
	}

	/**
	 * @return This is a type-1 plugin.
	 */
	@Override
	public final int getPluginType() {
		return 1;
	}

	/**
	 * Log the message.
	 * 
	 * @param level
	 *            The logging level.
	 * @param message
	 *            The logging message.
	 */
	@Override
	public final void log(final int level, final String message) {

		if (this.currentLevel < level) {
			final Date now = new Date();
			final StringBuilder line = new StringBuilder();
			line.append(now.toString());
			line.append(" [");
			switch (this.currentLevel) {
			case EncogLogging.LEVEL_CRITICAL:
				line.append("CRITICAL");
				break;
			case EncogLogging.LEVEL_ERROR:
				line.append("ERROR");
				break;
			case EncogLogging.LEVEL_INFO:
				line.append("INFO");
				break;
			case EncogLogging.LEVEL_DEBUG:
				line.append("DEBUG");
				break;
			default:
				line.append("?");
				break;
			}
			line.append("][");
			line.append(Thread.currentThread().getName());
			line.append("]: ");
			line.append(message);

			if (this.logConsole) {
				if (this.currentLevel > EncogLogging.LEVEL_ERROR) {
					System.err.println(line.toString());
				} else {
					System.out.println(line.toString());

				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void log(final int level, final Throwable t) {
		log(level, SystemLoggingPlugin.getStackTrace(t));
	}

	/**
	 * Set the logging level.
	 * 
	 * @param level
	 *            The logging level.
	 */
	public final void setLogLevel(final int level) {
		this.currentLevel = level;
	}

	/**
	 * Start logging to the console.
	 */
	public final void startConsoleLogging() {
		stopLogging();
		this.logConsole = true;
		setLogLevel(EncogLogging.LEVEL_DEBUG);
	}

	/**
	 * Stop any console or file logging.
	 */
	public final void stopLogging() {
		this.logConsole = false;
	}

}
