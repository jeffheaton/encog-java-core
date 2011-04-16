package org.encog.ml;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.encog.Encog;
import org.encog.util.csv.CSVFormat;

/**
 * A class that provides basic property functionality for the 
 * MLProperties interface.
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
	private final Map<String, String> properties = new HashMap<String, String>();
	
	/**
	 * @return A map of all properties.
	 */
	public Map<String, String> getProperties() {
		return this.properties;
	}

	/**
	 * Get the specified property as a double.
	 * 
	 * @param name
	 *            The name of the property.
	 * @return The property as a double.
	 */
	public double getPropertyDouble(final String name) {
		return Double.parseDouble(this.properties.get(name));
	}

	/**
	 * Get the specified property as a long.
	 * 
	 * @param name
	 *            The name of the specified property.
	 * @return The value of the specified property.
	 */
	public long getPropertyLong(final String name) {
		return Long.parseLong(this.properties.get(name));
	}

	/**
	 * Get the specified property as a string.
	 * 
	 * @param name
	 *            The name of the property.
	 * @return The value of the property.
	 */
	public String getPropertyString(final String name) {
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
	public void setProperty(final String name, final double d) {
		this.properties.put(name, "" + CSVFormat.EG_FORMAT.format(d, Encog.DEFAULT_PRECISION));
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
	public void setProperty(final String name, final long l) {
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
	public void setProperty(final String name, final String value) {
		this.properties.put(name, value);
		updateProperties();
	}
		
	public abstract void updateProperties();

}
