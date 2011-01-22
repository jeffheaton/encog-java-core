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
