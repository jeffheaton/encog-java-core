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
package org.encog.app.analyst;

import org.encog.ml.train.MLTrain;

/**
 * Reports the progress of the Encog Analyst. If you would like to use this with
 * an Encog StatusReportable object, use the bridge utilituy object:
 * 
 * org.encog.app.analyst.util.AnalystReportBridge
 * 
 */
public interface AnalystListener {

	/**
	 * Request stop the entire process.
	 */
	void requestShutdown();

	/**
	 * Request to cancel current command.
	 */
	void requestCancelCommand();

	/**
	 * @return True if the entire process should be stopped.
	 */
	boolean shouldShutDown();

	/**
	 * @return True if the current command should be stopped.
	 */
	boolean shouldStopCommand();

	/**
	 * Report that a command has begun.
	 * @param total The total parts.
	 * @param current The current part.
	 * @param name The name of that command.
	 */
	void reportCommandBegin(int total, int current, String name);

	/**
	 * Report that a command has ended.
	 * @param canceled True if this command was canceled.
	 */
	void reportCommandEnd(boolean canceled);

	/**
	 * Report that training has begun.
	 */
	void reportTrainingBegin();

	/**
	 * Report that training has ended.
	 */
	void reportTrainingEnd();

	/**
	 * Report progress on training.
	 * @param train The training object.
	 */
	void reportTraining(MLTrain train);

	/**
	 * Report progress on a task.
	 * @param total The total number of commands.
	 * @param current The current command.
	 * @param message The message.
	 */
	void report(int total, int current, String message);

}
