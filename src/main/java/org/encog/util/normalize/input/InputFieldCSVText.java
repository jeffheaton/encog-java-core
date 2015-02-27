/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.util.normalize.input;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * An input field based on a CSV file. This is a text field that is mapped to an
 * integer value. The first text value is mapped to 0, the second to 1, and so
 * on.
 */
public class InputFieldCSVText extends InputFieldCSV  {
	
	/**
	 * The mappings.
	 */
	private Map<String,Integer> mappings = new HashMap<String,Integer>();
	
	/**
	 * The current map index.
	 */
	private int currentMapIndex;

	/**
	 * Construct an InputFieldCSVText with the default constructor. This is mainly
	 * used for reflection.
	 */
	public InputFieldCSVText() {
		super();
	}

	/**
	 * Construct a input field for a CSV file.
	 * 
	 * @param usedForNetworkInput
	 *            True if this field is used for actual input to the neural
	 *            network, as opposed to segregation only.
	 * @param file
	 *            The tile to read.
	 * @param offset
	 *            The CSV file column to read.
	 */
	public InputFieldCSVText(final boolean usedForNetworkInput, final File file,
			final int offset) {
		super(usedForNetworkInput,file,offset);
	}
	
	/**
	 * Add a string mapping.
	 * @param name The name of the mapping.
	 * @return The index the mapping was assigned to.
	 */
	public int addMapping(String name) {
		this.mappings.put(name, currentMapIndex);
		int result = currentMapIndex;
		this.currentMapIndex++;
		return result;
	}

	public Map<String,Integer> getMappings() {
		return this.mappings;
	}
}
