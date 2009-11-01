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
package org.encog.normalize.segregate;

import org.encog.normalize.Normalization;

/**
 * Segregators are used to exclude certain rows. You may want to exclude rows to
 * create training and validation sets. You may also simply wish to exclude some
 * rows because they do not apply to what you are currently training for.
 */
public interface Segregator {
	
	/**
	 * @return The normalization object that is being used with this segregator.
	 */
	Normalization getNormalization();

	/**
	 * Setup this object to use the specified normalization object.
	 * @param normalization THe normalization object to use.
	 */
	void init(Normalization normalization);

	/**
	 * Should this row be included, according to this segregator.
	 * @return True if this row should be included.
	 */
	boolean shouldInclude();
}
