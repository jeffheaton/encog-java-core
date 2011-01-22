/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
