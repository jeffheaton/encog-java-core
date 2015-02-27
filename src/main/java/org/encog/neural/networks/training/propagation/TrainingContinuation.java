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
package org.encog.neural.networks.training.propagation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Allows training to be continued.
 * 
 */
public class TrainingContinuation implements Serializable {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = -3649790586015301015L;
			
	/**
	 * The contents of this object.
	 */
	private final Map<String, Object> contents = new HashMap<String, Object>();
	
	/**
	 * Training type.
	 */
	private String trainingType;

	/**
	 * Get an object by name.
	 * @param name The name of the object.
	 * @return The object requested.
	 */
	public Object get(final String name) {
		return this.contents.get(name);
	}

	/**
	 * @return The contents.
	 */
	public Map<String, Object> getContents() {
		return this.contents;
	}

	/**
	 * Save a list of doubles.
	 * @param key The key to save them under.
	 * @param list The list of doubles.
	 */
	public void put(final String key, final double[] list) {
		this.contents.put(key, list);
	}

	/**
	 * Set a value to a string.
	 * @param name The value to set.
	 * @param value The value.
	 */
	public void set(final String name, final Object value) {
		this.contents.put(name, value);
	}

	/**
	 * @return the trainingType
	 */
	public String getTrainingType() {
		return trainingType;
	}

	/**
	 * @param trainingType the trainingType to set
	 */
	public void setTrainingType(String trainingType) {
		this.trainingType = trainingType;
	}
	
	
}
