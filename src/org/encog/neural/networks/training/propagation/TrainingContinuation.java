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
	
	@Override
	public Persistor createPersistor() {
		return new TrainingContinuationPersistor();
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
		
	}

	@Override
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
