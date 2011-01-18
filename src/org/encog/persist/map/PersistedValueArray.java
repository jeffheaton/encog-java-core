package org.encog.persist.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.encog.Encog;
import org.encog.util.csv.CSVFormat;

public class PersistedValueArray extends PersistedProperty {
	
	private List<PersistedObject> list = new ArrayList<PersistedObject>();
	
	public PersistedValueArray(List<PersistedObject> list)
	{
		super(false);
		this.list = list;
	}
	
	public String toString()
	{
		return list.toString();
	}
	
	public String getString()
	{
		return toString();
	}

	@Override
	public Object getData() {
		return list;
	}

	public List<PersistedObject> getList() {
		return list;
	}
}
