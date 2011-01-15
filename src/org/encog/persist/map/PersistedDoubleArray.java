package org.encog.persist.map;

import java.util.Arrays;

import org.encog.Encog;
import org.encog.util.csv.CSVFormat;

public class PersistedDoubleArray extends PersistedProperty {
	private double[] data;
	
	public PersistedDoubleArray(double[] d)
	{
		super(false);
		this.data = d;
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

	public double[] getDoubleArray() {
		return data;
	}
}
