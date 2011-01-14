package org.encog.persist.map;

public class PersistedValue extends PersistedProperty {
	
	private String data;
	
	public PersistedValue(String str)
	{
		data = str;
	}
	
	public PersistedValue(double d)
	{
		this.data = Double.toString(d);
	}
	
	public String toString()
	{
		return data;
	}

	@Override
	public Object getData() {
		return data;
	}

	@Override
	String getString() {
		return data;
	}
}
