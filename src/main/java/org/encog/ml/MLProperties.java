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

import java.util.Map;

/**
 * Defines a Machine Learning Method that holds properties.
 */
public interface MLProperties extends MLMethod {

	/**
	 * @return A map of all properties.
	 */
	Map<String, String> getProperties();

	/**
	 * Get the specified property as a double.
	 * 
	 * @param name
	 *            The name of the property.
	 * @return The property as a double.
	 */
	double getPropertyDouble(final String name);

	/**
	 * Get the specified property as a long.
	 * 
	 * @param name
	 *            The name of the specified property.
	 * @return The value of the specified property.
	 */
	long getPropertyLong(final String name);

	/**
	 * Get the specified property as a string.
	 * 
	 * @param name
	 *            The name of the property.
	 * @return The value of the property.
	 */
	String getPropertyString(final String name);

	/**
	 * Set a property as a double.
	 * 
	 * @param name
	 *            The name of the property.
	 * @param d
	 *            The value of the property.
	 */
	void setProperty(final String name, final double d);

	/**
	 * Set a property as a long.
	 * 
	 * @param name
	 *            The name of the property.
	 * @param l
	 *            The value of the property.
	 */
	void setProperty(final String name, final long l);

	/**
	 * Set a property as a double.
	 * 
	 * @param name
	 *            The name of the property.
	 * @param value
	 *            The value of the property.
	 */
	void setProperty(final String name, final String value);
	
	/**
	 * Update any objeccts when a property changes.
	 */
	void updateProperties();
	
}
