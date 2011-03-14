package org.encog.ml;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.persist.BasicPersistedObject;
import org.encog.persist.map.PersistConst;
import org.encog.persist.map.PersistedObject;

public abstract class BasicML extends BasicPersistedObject implements MLMethod, MLProperties, Serializable {
	
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
		this.properties.put(name, "" + d);
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
	
	public void propertiesToMap(PersistedObject obj)
	{
		PersistedObject objProp = new PersistedObject();
		for( String key : this.properties.keySet() ) {
			String value = this.properties.get(key);
			objProp.setProperty(key, value, false);
		}
		obj.setProperty(PersistConst.PROPERTIES, objProp);		
	}
	
	public void propertiesFromMap(PersistedObject obj) {
		List<PersistedObject> propertiesList = obj.getPropertyValueArray(PersistConst.PROPERTIES);
		PersistedObject networkProperties = propertiesList.get(0);
		for(String key: networkProperties.getData().keySet() ) {
			this.setProperty(key, networkProperties.getPropertyString(key, true));
		}
	}
	
	public abstract void updateProperties();

}
