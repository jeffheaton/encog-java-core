package org.encog.script.javascript.objects;

import java.io.File;

import org.encog.persist.EncogCollection;
import org.encog.persist.EncogMemoryCollection;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.location.FilePersistence;

import sun.org.mozilla.javascript.internal.ScriptableObject;

public class JSEncogCollection extends ScriptableObject {
	
	private EncogMemoryCollection collection = new EncogMemoryCollection();
	
	@Override
	public String getClassName() {
		return "EncogCollection";
	}
	
	public void jsFunction_load(String filename)
	{
		this.collection.load(new FilePersistence(new File(filename)));
	}
	
	public void jsFunction_save(String filename)
	{
		this.collection.save(new FilePersistence(new File(filename)));
	}

	public EncogMemoryCollection getCollection() {
		return collection;
	}

	public void setCollection(EncogMemoryCollection collection) {
		this.collection = collection;
	}
	
	
	
}
