/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.cloud.indicator.basic;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Used to hold instruments, i.e. ticker symbols of securities.
 * Also holds financial data downloaded by ticker symbol.
 */
public class InstrumentHolder {
	
	/**
	 * The downloaded financial data.
	 */
	private Map<Long,String> data = new HashMap<Long,String>();
	
	/**
	 * The sorted data.
	 */
	private Set<Long> sorted = new TreeSet<Long>();
	
	/**
	 * Record one piece of data. Data with the same time stamp
	 * @param when The time the data occurred.
	 * @param starting Where should we start from when storing, index into data.
	 * Allows unimportant "leading data" to be discarded without creating a new
	 * array.
	 * @param data The financial data.
	 * @return True, if the data did not exist already.
	 */
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
