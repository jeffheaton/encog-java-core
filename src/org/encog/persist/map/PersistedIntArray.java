/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.persist.map;

import java.util.Arrays;

import org.encog.Encog;
import org.encog.util.csv.CSVFormat;

public class PersistedIntArray extends PersistedProperty {
	private int[] data;
	
	public PersistedIntArray(int[] d)
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

	public int[] getIntArray() {
		return data;
	}
}
