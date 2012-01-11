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
package org.encog.ml.genetic.innovation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides basic functionality for a list of innovations.
 */
public class BasicInnovationList implements InnovationList, Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The list of innovations.
	 */
	private final List<Innovation> list = new ArrayList<Innovation>();

	/**
	 * Add an innovation.
	 * @param innovation The innovation to add.
	 */
	public final void add(final Innovation innovation) {
		list.add(innovation);
	}

	/**
	 * Get a specific innovation, by index.
	 * @param id The innovation index id.
	 * @return The innovation.
	 */
	public final Innovation get(final int id) {
		return list.get(id);
	}

	/**
	 * @return A list of innovations.
	 */
	public final List<Innovation> getInnovations() {
		return list;
	}

}
