/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.neural.data;

import java.util.HashMap;
import java.util.Map;

import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.PropertyDataPersistor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An Encog data object that can be used to hold property data.  This is
 * a collection of name-value pairs that can be saved in an Encog 
 * persisted file.
 * @author jheaton
 *
 */
public class PropertyData implements EncogPersistedObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7940416732740995199L;
	private String name;
	private String description;
	private Map<String,String> data = new HashMap<String,String>();
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Map<String, String> getData() {
		return data;
	}

	public Persistor createPersistor() {
		return new PropertyDataPersistor();
	}
	
	public String get(String name)
	{
		return this.data.get(name);
	}
	
	public void set(String name,String value)
	{
		data.put(name,value);
	}
	
	public int size() {
		return this.data.size();
	}
	public Map<String, String> getMap() {
		return this.data;
	}
	public boolean isDefined(String key) {
		return this.data.containsKey(key);
	}
	public void remove(String key) {
		this.data.remove(key);	
	}
	
	public Object clone()
	{
		PropertyData result = new PropertyData();
		result.setName(getName());
		result.setDescription(getDescription());
		
		for(String key: data.keySet())
		{
			result.set(key, this.get(key));
		}
		return result;
	}
}
