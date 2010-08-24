package org.encog.script;

import org.encog.persist.EncogMemoryCollection;
import org.encog.script.javascript.EncogJavascriptEngine;

public class EncogScriptEngineFactory {
	public static EncogScriptEngine createEngine(EncogScript script)
	{
		if( script.getLanguage().equalsIgnoreCase(EncogScript.TYPE_JAVASCRIPT)) {
			if( script.getCollection() instanceof EncogMemoryCollection )
				return new EncogJavascriptEngine((EncogMemoryCollection)script.getCollection());
			else
				return new EncogJavascriptEngine();
		}
		else {
			throw new EncogScriptError("Uknown language: " + script.getLanguage());
		}
	}
}
