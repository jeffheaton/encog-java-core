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
package org.encog.util;

import java.util.Map;

import org.encog.EncogError;
import org.encog.util.csv.CSVFormat;

/**
 * A class that can be used to parse parameters stored in a map.  Allows the 
 * params to be accessed as various data types and to be validated.
 *
 */
public class ParamsHolder {
	
	/**
	 * The params that are to be parsed.
	 */
	private final Map<String,String> params;
	
	/**
	 * The format that numbers will be in.
	 */
	private final CSVFormat format;
	
	/**
	 * Construct the object. Allow the format to be specified.
	 * @param theParams The params to be used.
	 * @param theFormat The format to be used.
	 */
	public ParamsHolder(Map<String,String> theParams, CSVFormat theFormat) {
		this.params = theParams;
		this.format = theFormat;
	}
	
	/**
	 * Construct the object. Allow the format to be specified.
	 * @param theParams The params to be used.
	 */
	public ParamsHolder(Map<String,String> theParams) {
		this(theParams,CSVFormat.EG_FORMAT);
	}

	/**
	 * @return the params
	 */
	public Map<String, String> getParams() {
		return params;
	}
	
	/**
	 * Get a param as a string.
	 * @param name The name of the string.
	 * @param required True if this value is required.
	 * @param defaultValue The default value.
	 * @return The value.
	 */
	public String getString(String name, boolean required, String defaultValue) {
		if (this.params.containsKey(name)) {
			return this.params.get(name);
		} else {
			if (required) {
				throw new EncogError("Missing property: " + name);
			} else {
				return defaultValue;
			}
		}
	}
	
	/**
	 * Get a param as a integer.
	 * @param name The name of the integer.
	 * @param required True if this value is required.
	 * @param defaultValue The default value.
	 * @return The value.
	 */
	public int getInt(String name, boolean required, int defaultValue) {
		String str = getString(name,required,null);
		
		if( str==null )
			return defaultValue;
		
		try {
			return Integer.parseInt(str);
		} catch(NumberFormatException ex) {
			throw new EncogError("Property " + name + " has an invalid value of " + str + ", should be valid integer.");
		}
	}
	
	/**
	 * Get a param as a double.
	 * @param name The name of the double.
	 * @param required True if this value is required.
	 * @param defaultValue The default value. 
	 * @return The value.
	 */
	public double getDouble(String name, boolean required,double defaultValue) {
		String str = getString(name,required,null);
		
		if( str==null )
			return defaultValue;
		
		try {
			return this.format.parse(str);
		} catch(NumberFormatException ex) {
			throw new EncogError("Property " + name + " has an invalid value of " + str + ", should be valid floating point.");
		}
	}

	/**
	 * Get a param as a boolean.
	 * @param name The name of the double.
	 * @param required True if this value is required.
	 * @param defaultValue The default value.
	 * @return The value.
	 */
	public boolean getBoolean(String name, boolean required, boolean defaultValue) {
		
		String str = getString(name,required,null);
		
		if( str==null )
			return defaultValue;
		
		if( !str.equalsIgnoreCase("true") && !str.equalsIgnoreCase("false") ) {
			throw new EncogError("Property " + name + " has an invalid value of " + str + ", should be true/false.");
		}
		
		return str.equalsIgnoreCase("true");
	}
}
