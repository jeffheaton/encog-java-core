/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */
package org.encog.normalize.segregate.index;

import org.encog.persist.annotations.EGAttribute;

public class IndexRangeSegregator extends IndexSegregator {

	@EGAttribute
	private int startingIndex;

	@EGAttribute
	private int endingIndex;

	public IndexRangeSegregator(final int startingIndex, final int endingIndex) {
		this.startingIndex = startingIndex;
		this.endingIndex = endingIndex;
	}
	
	public IndexRangeSegregator()
	{
		
	}

	public int getEndingIndex() {
		return this.endingIndex;
	}

	public int getStartingIndex() {
		return this.startingIndex;
	}

	public boolean shouldInclude() {
		final boolean result = ((getCurrentIndex() >= this.startingIndex) && (getCurrentIndex() <= this.endingIndex));
		rollIndex();
		return result;
	}

}
