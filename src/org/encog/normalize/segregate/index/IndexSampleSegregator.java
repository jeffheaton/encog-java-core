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

package org.encog.normalize.segregate.index;

import org.encog.persist.annotations.EGAttribute;

/**
 * An index segregator is used to segregate the data according to its index.
 * Nothing about the data is actually compared. This makes the index range
 * segregator very useful for breaking the data into training and validation
 * sets. For example, you could very easily determine that 70% of the data is
 * for training, and 30% for validation.
 * 
 * This segregator takes a starting and ending index, as well as a smple size.
 * Everything that is between these two indexes will be used.  The sample 
 * repeats over and over.  For example, if you choose a sample size of 10, 
 * and a beginning index of 0 and an ending index of 5, you would get
 * half of the first 10 element, then half of the next ten, and so on.
 * 
 */
public class IndexSampleSegregator extends IndexSegregator {

	/**
	 * The starting index (within a sample).
	 */
	@EGAttribute
	private int startingIndex;

	/**
	 * The ending index (within a sample).
	 */
	@EGAttribute
	private int endingIndex;

	/**
	 * The sample size.
	 */
	@EGAttribute
	private int sampleSize;

	/**
	 * The default constructor, for reflection.
	 */
	public IndexSampleSegregator() {
	}

	/**
	 * Construct an index sample segregator.
	 * @param startingIndex The starting index.
	 * @param endingIndex The ending index.
	 * @param sampleSize The sample size.
	 */
	public IndexSampleSegregator(final int startingIndex,
			final int endingIndex, final int sampleSize) {
		this.sampleSize = sampleSize;
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
	 * @return The sample size.
	 */
	public int getSampleSize() {
		return this.sampleSize;
	}

	/**
	 * @return The starting index.
	 */
	public int getStartingIndex() {
		return this.startingIndex;
	}

	/**
	 * Should this row be included.
	 * @return True if this row should be included.
	 */
	public boolean shouldInclude() {
		final int sampleIndex = getCurrentIndex() % this.sampleSize;
		rollIndex();
		return ((sampleIndex >= this.startingIndex) 
				&& (sampleIndex <= this.endingIndex));
	}
	
	/**
	 * Nothing needs to be done to setup for a pass.
	 */
	public void passInit() {		
	}

}
