package org.encog.script;

import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;

import org.encog.persist.EncogCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.EncogScriptPersistor;
import org.encog.script.basic.Basic;
import org.encog.script.basic.BasicModule;
import org.encog.script.basic.BasicParse;
import org.encog.script.basic.BasicVariable;
import org.encog.script.basic.Console;
import org.encog.script.basic.Err;

public class EncogScript implements EncogPersistedObject {

	private String name;
	private String description;
	private String source;
	private BasicModule module;
	
	/**
	 * The Encog collection this object belongs to, or null if none.
	 */
	private EncogCollection encogCollection;

	
	@Override
	public Persistor createPersistor() {
		return new EncogScriptPersistor();
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

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}
	
	/**
	 * @return The collection this Encog object belongs to, null if none.
	 */
	public EncogCollection getCollection() {
		return this.encogCollection;
	}

	/**
	 * Set the Encog collection that this object belongs to.
	 */
	public void setCollection(EncogCollection collection) {
		this.encogCollection = collection; 
	}
	
	public void load()
	{
		this.module = new BasicModule();
		this.module.Load(this);
	}

}
