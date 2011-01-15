package org.encog.persist.map;

import java.io.Serializable;

public abstract class PersistedProperty implements Serializable {
	
	private final boolean attribute;
	
	public abstract Object getData();
	public abstract String getString();
	
	public PersistedProperty(boolean attribute) {
		this.attribute = attribute;
	}
	
	public boolean isAttribute() {
		return attribute;
	}
}
