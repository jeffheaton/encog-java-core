/*
 * Encog Neural Network and Bot Library for Java v1.x
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008, Heaton Research Inc., and individual contributors.
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

package org.encog.util.time;

/**
 * Get the name or code for a time unit.
 * 
 * @author jheaton
 */
public interface TimeUnitNames {

	/**
	 * Get the code for the specified time unit.
	 * 
	 * @param unit
	 *            The time unit.
	 * @return Return the code for the specified time unit.
	 */
	String code(TimeUnit unit);

	/**
	 * Get the plural name for the specified time unit.
	 * 
	 * @param unit
	 *            The time unit.
	 * @return Return the plural name for the specified unit.
	 */
	String plural(TimeUnit unit);

	/**
	 * Get the singular form of the specified time unit.
	 * 
	 * @param unit
	 *            The time unit.
	 * @return The singular form of the specified time unit.
	 */
	String singular(TimeUnit unit);
}
