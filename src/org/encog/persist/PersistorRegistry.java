package org.encog.persist;

import java.util.HashMap;
import java.util.Map;

import org.encog.ml.svm.PersistSVM;

public class PersistorRegistry {
	
	private static PersistorRegistry instance;
	private Map<String,EncogPersistor> map = new HashMap<String,EncogPersistor>();
	
	private PersistorRegistry() {
		add(new PersistSVM());
	}

	public void add(PersistSVM persistor) {
		map.put(persistor.getPersistClassString(), persistor);		
	}
	
	public static PersistorRegistry getInstance() {
		if( instance==null ) {
			instance = new PersistorRegistry();
		}
		
		return instance;
	}
	
	public EncogPersistor getPersistor(String name) {
		return map.get(name);
	}
	
	public EncogPersistor getPersistor(Class<?> clazz) {
		return getPersistor(clazz.getSimpleName());
	}
}
