package org.encog.persist.map;

import java.util.Arrays;

import org.encog.Encog;
import org.encog.util.csv.CSVFormat;

public class PersistedValueArray extends PersistedProperty {
	
	private String[] data;
	
	public PersistedValueArray(double[] d)
	{
		data = new String[d.length];
		for(int i=0;i<d.length;i++)
		{
			data[i] = CSVFormat.EG_FORMAT.format(d[i], Encog.DEFAULT_PRECISION);
		}
	}
	
	public String toString()
	{
		return Arrays.toString(data);
	}
	
	public String getString()
	{
		return Arrays.toString(data);
	}

	@Override
	public Object getData() {
		return data;
	}
}
