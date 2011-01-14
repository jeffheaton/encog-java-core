package org.encog.persist.map;

import java.util.Arrays;

import org.encog.Encog;
import org.encog.util.csv.CSVFormat;

public class PersistedValueArray extends PersistedProperty {
	
	private String[] data;
	
	public PersistedValueArray(double[] d)
	{
		super(false);
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
		StringBuilder result = new StringBuilder();
		for(int i = 0; i<data.length;i++)
		{
			if( result.length()>0 )
				result.append(',');
			result.append(data[i]);
		}
		return result.toString();
	}

	@Override
	public Object getData() {
		return data;
	}
}
