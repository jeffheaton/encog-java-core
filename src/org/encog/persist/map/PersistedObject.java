package org.encog.persist.map;

import java.util.HashMap;
import java.util.Map;

import org.encog.parse.ParseError;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.PersistError;

public class PersistedObject extends PersistedProperty {
	
	private final Map<String,PersistedProperty> data = new HashMap<String,PersistedProperty>();
	private String objectType;
	
	public PersistedObject()
	{
		super(false);
	}
	
	
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
	public void setProperty(String name, String value, boolean attribute)
	{
		if( value!=null )
		{
			this.data.put(name, new PersistedValue(value,attribute));
		}
	}

	/**
	 * Set a property as a double value.
	 * @param name The name of the property.
	 * @param value The value to set to.
	 */
	public void setProperty(String name, int value, boolean attribute)
	{
			this.data.put(name, new PersistedValue(value,attribute));
	}
	
	/**
	 * Set a property as a double value.
	 * @param name The name of the property.
	 * @param value The value to set to.
	 */
	public void setProperty(String name, double value, boolean attribute)
	{
			this.data.put(name, new PersistedValue(value,attribute));
	}
	
	/**
	 * Set a property as a double array value.
	 * @param name The name of the property.
	 * @param value The value to set to.
	 */
	public void setProperty(String name, double[] value)
	{
		if( value!=null )
		{
			this.data.put(name, new PersistedDoubleArray(value));
		}
	}
	

	public void setStandardProperties(EncogPersistedObject obj) {
		setProperty(PersistConst.NAME, obj.getName(),true);
		setProperty(PersistConst.DESCRIPTION, obj.getDescription(),true);
		setProperty(PersistConst.NATIVE, obj.getClass().toString(),true);
		
	}

	public void requireType(String t) {
		if( !t.equals(this.objectType) )
		{
			throw new PersistError("Expected object type: " + t + ", but found: " + this.objectType);
		}
		
	}
	
	private boolean require(String name,boolean required)
	{
		if( !this.data.containsKey(name) )
		{
			if( required )
				throw new PersistError("The property " + name + " was required.");
			return true;
		}
		return false;
	}

	public String getPropertyString(String name, boolean required)
	{
		if( require(name,required) )
		{
			return null;
		}
		PersistedProperty result = this.data.get(name);
		return result.getString();
	}
	
	public double[] getPropertyDoubleArray(String name, boolean required) {
		try
		{
			if( require(name,required) )
			{
				return null;
			}
			
			PersistedProperty result = this.data.get(name);
			if( result instanceof PersistedDoubleArray )
			{
				PersistedDoubleArray a = (PersistedDoubleArray)result;
				return a.getDoubleArray();
			}
			throw new PersistError("Expected double array for " + name);
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


	public void setPropertyList(String name, String str) {
		System.out.println( name + " - " + str);
		
	}


	public int getPropertyInt(String name, boolean required) {
		String str = this.getPropertyString(name, required);
		try {
			return Integer.parseInt(str);
		}
		catch(NumberFormatException ex) {
			throw new PersistError("Property: " + name + ", had invalid integer:" + str );
		}
	}


	public double getPropertyDouble(String name, boolean required) {
		String str = this.getPropertyString(name, required);
		try {
			return Double.parseDouble(str);
		}
		catch(NumberFormatException ex) {
			throw new PersistError("Property: " + name + ", had invalid double:" + str );
		}
	}
}
