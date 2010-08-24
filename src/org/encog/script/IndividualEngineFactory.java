package org.encog.script;

import org.encog.persist.EncogMemoryCollection;

public interface IndividualEngineFactory {
	String getName();
	EncogScriptEngine create(EncogMemoryCollection collection);
	EncogScriptEngine create();
}
