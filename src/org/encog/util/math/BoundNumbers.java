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
package org.encog.util.math;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple class that prevents numbers from getting either too
 * big or too small.
 */
public final class BoundNumbers {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Private constructor.
	 */
	private BoundNumbers() {
		
	}
	
	/**
	 * Too small of a number.
	 */
	public static final double TOO_SMALL = -1.0E20;

	/**
	 * Too big of a number.
	 */
	public static final double TOO_BIG = 1.0E20;

	/**
	 * Bound the number so that it does not become too big or too small.
	 * 
	 * @param d
	 *            The number to check.
	 * @return The new number. Only changed if it was too big or too small.
	 */
	public static double bound(final double d) {
		if (d < TOO_SMALL) {
			return TOO_SMALL;
		} else if (d > TOO_BIG) {
			return TOO_BIG;
		} else {
			return d;
		}
	}
}
