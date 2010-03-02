/*
 * Encog(tm) Core v2.4
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
 * This segregator takes a starting and ending index. Everything that is between
 * these two indexes will be used.
 * 
 */
public class IndexRangeSegregator extends IndexSegregator {

	/**
	 * The starting index.
	 */
	@EGAttribute
	private int startingIndex;

	/**
	 * The ending index.
	 */
	@EGAttribute
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
	
	/**
	 * Nothing needs to be done to setup for a pass.
	 */
	public void passInit() {		
	}

}
