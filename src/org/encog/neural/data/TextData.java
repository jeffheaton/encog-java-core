package org.encog.neural.data;

import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.neural.persist.persistors.TextDataPersistor;

public class TextData implements EncogPersistedObject {
	private String text;
	private String name;
	private String description;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
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

	public Persistor createPersistor() {		
		return new TextDataPersistor();
	}
	
	
}
