package org.encog.persist.map;

import java.util.HashMap;
import java.util.Map;

import org.encog.parse.ParseError;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.PersistError;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public class PersistedObject extends PersistedProperty {
	
	private final Map<String,PersistedProperty> data = new HashMap<String,PersistedProperty>();
	private String objectType;
	
	/**
	 * @return the data
	 */
	public Map<String, PersistedProperty> getData() {
		return data;
	}
	
	public void clear(String objectType)
	{
		this.objectType = objectType;
		this.data.clear();
	}
	
	/**
	 * @return the objectType
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * Set a property as a string value.
	 * @param name The name of the property.
	 * @param value The value to set to.
	 */
	public void setProperty(String name, String value)
	{
		this.data.put(name, new PersistedValue(value));
	}
	
	/**
	 * Set a property as a double value.
	 * @param name The name of the property.
	 * @param value The value to set to.
	 */
	public void setProperty(String name, double value)
	{
		this.data.put(name, new PersistedValue(value));
	}
	
	/**
	 * Set a property as a double array value.
	 * @param name The name of the property.
	 * @param value The value to set to.
	 */
	public void setProperty(String name, double[] value)
	{
		this.data.put(name, new PersistedValueArray(value));
	}
	

	public void setStandardProperties(EncogPersistedObject obj) {
		setProperty(PersistConst.NAME, obj.getName());
		setProperty(PersistConst.DESCRIPTION, obj.getDescription());
		setProperty(PersistConst.NATIVE, obj.getClass().toString());
		
	}

	public void requireType(String t) {
		if( !t.equals(this.objectType) )
		{
			throw new PersistError("Expected object type: " + t + ", but found: " + this.objectType);
		}
		
	}

	public String getPropertyString(String name, boolean required)
	{
		if( !this.data.containsKey(name) )
		{
			if( required )
				throw new PersistError("The property " + name + " was required.");
			return null;
		}
		
		PersistedProperty result = this.data.get(name);
		return result.getString();
	}
	
	public double[] getPropertyDoubleArray(String name, boolean required) {
		String str = getPropertyString(name,required);
		try
		{
			double[] result = NumberList.fromList(CSVFormat.EG_FORMAT, str);
			return result;
		}
		catch(Exception e)
		{
			throw new ParseError("Invalid double array: " + name);
		}
	}

	@Override
	public String getString() {
		return this.objectType;
	}
}
