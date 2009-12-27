/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
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
package org.encog.neural.networks.training.propagation;

import java.util.HashMap;
import java.util.Map;

import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.TrainingContinuationPersistor;

public class TrainingContinuation implements EncogPersistedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3649790586015301015L;
	private String name;
	private String description;
	private Map<String,Object> contents = new HashMap<String,Object>();
	
	public Persistor createPersistor() {
		return new TrainingContinuationPersistor();
	}

	public String getDescription() {
		return this.description;
	}

	public String getName() {
		return this.name;
	}

	public void setDescription(String description) {
		this.description = description;
		
	}

	public void setName(String name) {
		this.name = name;		
	}
	
	public Map<String,Object> getContents()
	{
		return this.contents;
	}
	
	public void set(String name, Object value) {
		this.contents.put(name, value);
	}
	
	public Object get(String name) {
		return this.contents.get(name);
	}

	public void put(String key, double[] list) {
		this.contents.put(key,list);		
	}

}
