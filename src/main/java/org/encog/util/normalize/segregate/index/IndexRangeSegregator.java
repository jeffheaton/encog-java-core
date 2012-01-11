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


/**
 * An index segregator is used to segregate the data according to its index.
 * Nothing about the data is actually compared. This makes the index range
 * segregator very useful for breaking the data into training and validation
 * sets. For example, you could very easily determine that 70% of the data is
 * for training, and 30% for validation.
 * 
 * This segregator takes a starting and ending index. Everything that is between
 * these two indexes will be used.
 * 
 */
public class IndexRangeSegregator extends IndexSegregator {

	/**
	 * The starting index.
	 */
	private int startingIndex;

	/**
	 * The ending index.
	 */
	private int endingIndex;

	/**
	 * Default constructor for reflection.
	 */
	public IndexRangeSegregator() {

	}

	/**
	 * Construct an index range segregator.
	 * @param startingIndex The starting index to allow.
	 * @param endingIndex The ending index to allow.
	 */
	public IndexRangeSegregator(final int startingIndex, 
			final int endingIndex) {
		this.startingIndex = startingIndex;
		this.endingIndex = endingIndex;
	}

	/**
	 * @return The ending index.
	 */
	public int getEndingIndex() {
		return this.endingIndex;
	}

	/**
	 * @return The starting index.
	 */
	public int getStartingIndex() {
		return this.startingIndex;
	}

	/**
	 * Determines if the current row should be included.
	 * @return True if the current row should be included.
	 */
	public boolean shouldInclude() {
		final boolean result = ((getCurrentIndex() >= this.startingIndex) 
				&& (getCurrentIndex() <= this.endingIndex));
		rollIndex();
		return result;
	}
}
