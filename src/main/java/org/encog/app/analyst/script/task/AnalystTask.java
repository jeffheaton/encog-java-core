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
package org.encog.app.analyst.script.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a task in the script. A task is a named set of commands.
 * 
 */
public class AnalystTask {

	/**
	 * The name of the task.
	 */
	private String name;

	/**
	 * The "source code" for this task.
	 */
	private final List<String> lines = new ArrayList<String>();

	/**
	 * Construct an analyst task.
	 * 
	 * @param theName
	 *            The name of this task.
	 */
	public AnalystTask(final String theName) {
		this.name = theName;
	}

	/**
	 * @return the lines
	 */
	public final List<String> getLines() {
		return this.lines;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * @param theName
	 *            the name to set
	 */
	public final void setName(final String theName) {
		this.name = theName;
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" name=");
		result.append(this.name);
		result.append("]");
		return result.toString();
	}

}
