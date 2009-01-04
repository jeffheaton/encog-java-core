package org.encog.neural.data;

import java.util.HashMap;
import java.util.Map;

import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.neural.persist.persistors.PropertyDataPersistor;

public class PropertyData implements EncogPersistedObject {
	private String name;
	private String description;
	private Map<String,String> data = new HashMap<String,String>();
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
}
