package org.encog.script;

import java.util.HashMap;
import java.util.Map;

import org.encog.persist.EncogMemoryCollection;

public class EncogScriptEngineFactory {
	
	private static EncogScriptEngineFactory instance;
	private Map<String,IndividualEngineFactory> map = new HashMap<String,IndividualEngineFactory>();
	
	public static EncogScriptEngineFactory getInstance()
	{
		if( instance==null )
			instance = new EncogScriptEngineFactory();
		
		return instance;
	}
	
	public void registerIndividualEngineFactory(IndividualEngineFactory factory)
	{
		String key = factory.getName().toLowerCase();
		this.map.put(key,factory);
	}
	
	public EncogScriptEngine createEngine(EncogScript script)
	{
		String key = script.getLanguage().toLowerCase();
		IndividualEngineFactory factory = this.map.get(key);
		if( factory==null ) {
			throw new EncogScriptError("Uknown language: " + script.getLanguage());
		}
		else {
			if( script.getCollection() instanceof EncogMemoryCollection )
				return factory.create((EncogMemoryCollection)script.getCollection());
			else
				return factory.create();
		}
	}
}
