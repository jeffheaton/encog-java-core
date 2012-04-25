package org.encog.cloud.indicator.basic;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class InstrumentHolder {
	
	private Map<Long,String> data = new HashMap<Long,String>();
	private Set<Long> sorted = new TreeSet<Long>();
	
	public boolean record(long when, int starting, String[] data)
	{
		boolean result;
		StringBuilder str = new StringBuilder();
		
		for(int i=starting;i<data.length;i++) {
			if( i>starting ) {
				str.append(',');
			}
			str.append(data[i]);
		}
		
		Long key = new Long(when);
		
		result = !this.data.containsKey(key);
		this.sorted.add(key);
		this.data.put(key, str.toString());
		return result;
	}

	/**
	 * @return the data
	 */
	public Map<Long, String> getData() {
		return data;
	}

	/**
	 * @return the sorted
	 */
	public Set<Long> getSorted() {
		return sorted;
	}

	
	
}
