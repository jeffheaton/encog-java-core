package org.encog.ml;

import java.util.Map;

import org.encog.persist.map.PersistedObject;

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
	
	void propertiesToMap(PersistedObject obj);
	
	void propertiesFromMap(PersistedObject obj);
	
	void updateProperties();
	
}
