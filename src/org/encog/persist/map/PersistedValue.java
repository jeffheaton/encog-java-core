package org.encog.persist.map;

import org.encog.Encog;
import org.encog.util.csv.CSVFormat;

public class PersistedValue extends PersistedProperty {
	
	private String data;
	
	public PersistedValue(String str, boolean attribute)
	{
		super(attribute);
		data = str;
	}
	
	public PersistedValue(double d, boolean attribute)
	{
		super(attribute);
		this.data = CSVFormat.EG_FORMAT.format(d, Encog.DEFAULT_PRECISION);
	}
	
	public PersistedValue(int d, boolean attribute)
	{
		super(attribute);
		this.data = Integer.toString(d);
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
	public String getString() {
		return data;
	}
}
