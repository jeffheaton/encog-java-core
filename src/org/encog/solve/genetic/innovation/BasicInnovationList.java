/*
 * Encog(tm) Core v2.5 
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

package org.encog.solve.genetic.innovation;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides basic functionality for a list of innovations.
 */
public class BasicInnovationList implements InnovationList {

	/**
	 * The list of innovations.
	 */
	private final List<Innovation> list = new ArrayList<Innovation>();

	/**
	 * Add an innovation.
	 * @param innovation The innovation to add.
	 */
	public void add(final Innovation innovation) {
		list.add(innovation);
	}

	/**
	 * Get a specific innovation, by index.
	 * @param id The innovation index id.
	 * @return The innovation.
	 */
	public Innovation get(final int id) {
		return list.get(id);
	}

	/**
	 * @return A list of innovations.
	 */
	public List<Innovation> getInnovations() {
		return list;
	}

}
