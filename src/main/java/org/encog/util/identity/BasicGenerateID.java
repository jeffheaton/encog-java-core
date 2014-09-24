/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.util.identity;

import java.io.Serializable;

/**
 * Used to generate a unique id.
 * 
 */
public class BasicGenerateID implements GenerateID, Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The current ID to generate.
	 */
	private long currentID;

	/**
	 * Construct the ID generator to start at 1.
	 */
	public BasicGenerateID() {
		synchronized (this) {
			this.currentID = 1;
		}
	}

	/**
	 * Generate the next ID.
	 * @return The next ID.
	 */
	public long generate() {
		synchronized (this) {
			return this.currentID++;
		}
	}

	/**
	 * @return the currentID
	 */
	public long getCurrentID() {
		synchronized (this) {
			return currentID;
		}
	}

	/**
	 * @param currentID the currentID to set
	 */
	public void setCurrentID(long currentID) {
		synchronized (this) {
			this.currentID = currentID;
		}
	}
	
	
}
