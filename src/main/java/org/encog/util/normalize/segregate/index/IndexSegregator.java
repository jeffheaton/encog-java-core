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
package org.encog.util.normalize.segregate.index;

import org.encog.util.normalize.DataNormalization;
import org.encog.util.normalize.segregate.Segregator;

/**
 * The index segregator. An abstract class to build index based segregators off
 * of. An index segregator is used to segregate the data according to its index.
 * Nothing about the data is actually compared. This makes the index range
 * segregator very useful for breaking the data into training and validation
 * sets. For example, you could very easily determine that 70% of the data is
 * for training, and 30% for validation.
 */
public abstract class IndexSegregator implements Segregator {

	/**
	 * The current index.  Updated rows are processed.
	 */
	private int currentIndex = 0;

	/**
	 * THe normalization object this belongs to.
	 */
	private DataNormalization normalization;

	/**
	 * @return The current index.
	 */
	public int getCurrentIndex() {
		return this.currentIndex;
	}

	/**
	 * @return The normalization object this object will use.
	 */
	public DataNormalization getNormalization() {
		return this.normalization;
	}

	/**
	 * Setup this class with the specified normalization object.
	 * @param normalization Normalization object.
	 */
	public void init(final DataNormalization normalization) {
		this.normalization = normalization;
	}

	/**
	 * Used to increase the current index as data is processed.
	 */
	public void rollIndex() {
		this.currentIndex++;
	}
	
	/**
	 * Reset the counter to zero.
	 */
	public void passInit() {	
		this.currentIndex = 0;
	}

}
