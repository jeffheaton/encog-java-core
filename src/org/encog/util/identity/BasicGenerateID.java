/*
 * Encog(tm) Core v2.6 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

import org.encog.persist.annotations.EGAttribute;

/**
 * Used to generate a unique id.
 * 
 */
public class BasicGenerateID implements GenerateID {

	/**
	 * The current ID to generate.
	 */
	@EGAttribute
	private long currentID;

	/**
	 * Construct the ID generator to start at 1.
	 */
	public BasicGenerateID() {
		this.currentID = 1;
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
}
