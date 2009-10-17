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

import org.encog.normalize.Normalization;
import org.encog.normalize.segregate.Segregator;
import org.encog.persist.annotations.EGIgnore;
import org.encog.persist.annotations.EGReference;

public abstract class IndexSegregator implements Segregator {

	@EGIgnore
	private int currentIndex = 0;
	
	@EGReference
	private Normalization normalization;

	public int getCurrentIndex() {
		return this.currentIndex;
	}

	public Normalization getNormalization() {
		return this.normalization;
	}

	public void init(final Normalization normalization) {
		this.normalization = normalization;
	}

	public void rollIndex() {
		this.currentIndex++;
	}

}
