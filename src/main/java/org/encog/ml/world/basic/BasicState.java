package org.encog.ml.world.basic;

import java.util.HashMap;
import java.util.Map;

import org.encog.ml.world.State;

public class BasicState implements State {

	private final Map<String, Object> properties = new HashMap<String, Object>();
	
	@Override
	public void setProperty(String key, Object value) {
		this.properties.put(key, value);		
	}

	@Override
	public Object getProperty(String key) {
		return this.properties.get(key);
	}
	
}
