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
package org.encog.ml;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.encog.Encog;
import org.encog.util.csv.CSVFormat;

/**
 * A class that provides basic property functionality for the MLProperties
 * interface.
 */
public abstract class BasicML implements MLMethod, MLProperties, Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Properties about the neural network. Some NeuralLogic classes require
	 * certain properties to be set.
	 */
	private final Map<String, String> properties 
		= new HashMap<String, String>();

	/**
	 * @return A map of all properties.
	 */
	@Override
	public final Map<String, String> getProperties() {
		return this.properties;
	}

	/**
	 * Get the specified property as a double.
	 * 
	 * @param name
	 *            The name of the property.
	 * @return The property as a double.
	 */
	@Override
	public final double getPropertyDouble(final String name) {
		return Double.parseDouble(this.properties.get(name));
	}

	/**
	 * Get the specified property as a long.
	 * 
	 * @param name
	 *            The name of the specified property.
	 * @return The value of the specified property.
	 */
	@Override
	public final long getPropertyLong(final String name) {
		return Long.parseLong(this.properties.get(name));
	}

	/**
	 * Get the specified property as a string.
	 * 
	 * @param name
	 *            The name of the property.
	 * @return The value of the property.
	 */
	@Override
	public final String getPropertyString(final String name) {
		return this.properties.get(name);
	}

	/**
	 * Set a property as a double.
	 * 
	 * @param name
	 *            The name of the property.
	 * @param d
	 *            The value of the property.
	 */
	@Override
	public final void setProperty(final String name, final double d) {
		this.properties.put(name,
				"" + CSVFormat.EG_FORMAT.format(d, Encog.DEFAULT_PRECISION));
		updateProperties();
	}

	/**
	 * Set a property as a long.
	 * 
	 * @param name
	 *            The name of the property.
	 * @param l
	 *            The value of the property.
	 */
	@Override
	public final void setProperty(final String name, final long l) {
		this.properties.put(name, "" + l);
		updateProperties();
	}

	/**
	 * Set a property as a double.
	 * 
	 * @param name
	 *            The name of the property.
	 * @param value
	 *            The value of the property.
	 */
	@Override
	public final void setProperty(final String name, final String value) {
		this.properties.put(name, value);
		updateProperties();
	}

	@Override
	public abstract void updateProperties();

}
